package com.hus.englishapp.kuro.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.hus.englishapp.kuro.config.MessageTemplate;
import com.hus.englishapp.kuro.exception.AppException;
import com.hus.englishapp.kuro.model.dto.AuthRequest;
import com.hus.englishapp.kuro.model.User;
import com.hus.englishapp.kuro.repository.UserRepository;
import com.hus.englishapp.kuro.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final MessageTemplate messageTemplate;

    public ResponseEntity<?> authenticateAndGenerateToken(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                // Lưu Authentication vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Lấy thông tin UserDetails từ authentication
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String username = userDetails.getUsername();

                // Tạo Access Token và Refresh Token
                String accessToken = jwtService.generateToken(username);
                String refreshToken = jwtService.generateRefreshToken(username);

                // Lưu Refresh Token vào database
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                user.setRefreshToken(refreshToken);
                userRepository.save(user);

                // Trả về Access Token và đặt Refresh Token vào HttpOnly Cookie
                ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                        .httpOnly(true)
                        .secure(true) // Chỉ hoạt động với HTTPS
                        .sameSite("None") // nếu frontend và backend khác domain
                        .path("/") // Chỉ gửi với request tới /refresh
                        .maxAge(7 * 24 * 60 * 60) // 7 ngày
                        .build();


                // Trả về cả Access Token & Refresh Token
                Map<String, String> tokens = new HashMap<>();
                tokens.put("token", accessToken);
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                        .body(tokens);
            }
        } catch (BadCredentialsException ex) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", messageTemplate.message("error.UserController.passwordIncorrect")));
        } catch (UsernameNotFoundException ex) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", messageTemplate.message("error.UserController.usernameNotExist")));
        } catch (Exception ex) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", messageTemplate.message("error.UserController.errorAuthen") + ex.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", messageTemplate.message("error.UserController.errorAuthen")));
    }

    public ResponseEntity<?> refreshToken(String oldRefreshToken) {
        User user = userRepository.findByRefreshToken(oldRefreshToken)
                .orElseThrow(() -> new AppException("Invalid refresh token", HttpStatus.UNAUTHORIZED));

        // Kiểm tra refresh token hết hạn (nếu có cơ chế expiry)
        if (jwtService.isTokenExpired(oldRefreshToken)) {
            throw new AppException("Refresh token expired", HttpStatus.FORBIDDEN);
        }

        // Tạo cặp token mới
        String newAccessToken = jwtService.generateToken(user.getUsername());
        String newRefreshToken = jwtService.generateRefreshToken(user.getUsername());

        // Cập nhật refresh token mới vào DB
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        // Trả về Access Token và đặt Refresh Token vào HttpOnly Cookie
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true) // Chỉ hoạt động với HTTPS
                .sameSite("None") // nếu frontend và backend khác domain
                .path("/") // Chỉ gửi với request tới /refresh
                .maxAge(7 * 24 * 60 * 60) // 7 ngày
                .build();

        // Trả về cả Access Token & Refresh Token
        Map<String, String> tokens = new HashMap<>();
        tokens.put("token", newAccessToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(tokens);
    }

    public String verifyGoogleToken(String idTokenString, String action) throws GeneralSecurityException, IOException {
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance(); // Hoặc DefaultJsonFactory.getDefaultInstance()
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                jsonFactory
        ).setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            if (Objects.equals(action, Constants.TYPE_ACCOUNT.GOOGLE_TYPE.LOGIN_GOOGLE)) {
                // Kiểm tra và tạo user nếu chưa tồn tại
                User user = userRepository.findByEmailAndProvider(email, Constants.TYPE_ACCOUNT.GOOGLE).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setAvatar((String) payload.get("picture"));
                    newUser.setProvider(Constants.TYPE_ACCOUNT.GOOGLE);
                    newUser.setFullname((String) payload.get("name"));
                    newUser.setRoles("ROLE_USER");
                    newUser.setUsername(email);
                    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    newUser.setId(UUID.randomUUID().toString());
                    return userRepository.save(newUser);
                });
            }
            return email;
        }
        throw new SecurityException("Invalid Google Token");
    }

    public void saveRefreshToken(String email, String refreshToken, String typeAccount) {
        User user = userRepository.findByEmailAndProvider(email, typeAccount)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    public boolean logout(String oldRefreshToken) {
        Optional<User> userOptional = userRepository.findByRefreshToken(oldRefreshToken);

        if (userOptional.isEmpty()) {
            return false; // Token không tồn tại
        }

        User user = userOptional.get();
        user.setRefreshToken(null);
        userRepository.save(user);

        return true;
    }
}
