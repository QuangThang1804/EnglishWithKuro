package com.hus.englishapp.kuro.filter;

import com.hus.englishapp.kuro.service.JwtService;
import com.hus.englishapp.kuro.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService authUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Bỏ qua các endpoint OAuth2
            if (isOAuth2Request(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = extractToken(request);
            if (token != null) {
                String userIdentifier = jwtService.extractUsername(token);

                if (userIdentifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = authUserDetailsService.loadUserByIdentifier(userIdentifier);

                    if (jwtService.validateToken(token, userDetails)) {
                        setAuthentication(userDetails, request);
                    }
                }
            }
        } catch (ExpiredJwtException e) {
            handleTokenExpired(response);
            return;
        } catch (Exception e) {
            logger.error("Authentication error", e);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isOAuth2Request(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/oauth2/") ||
                request.getRequestURI().startsWith("/login/oauth2/code/");
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return (authHeader != null && authHeader.startsWith("Bearer ")) ?
                authHeader.substring(7) : null;
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void handleTokenExpired(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"JWT token has expired\"}");
    }
}