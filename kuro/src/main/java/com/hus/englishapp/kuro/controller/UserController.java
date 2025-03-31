package com.hus.englishapp.kuro.controller;

import com.hus.englishapp.kuro.config.MessageTemplate;
import com.hus.englishapp.kuro.exception.AppException;
import com.hus.englishapp.kuro.model.AuthRequest;
import com.hus.englishapp.kuro.model.SectionContent;
import com.hus.englishapp.kuro.model.User;
import com.hus.englishapp.kuro.model.dto.RefreshTokenRequest;
import com.hus.englishapp.kuro.model.dto.UserDto;
import com.hus.englishapp.kuro.model.dto.UserRequestDto;
import com.hus.englishapp.kuro.model.dto.UserResponseDto;
import com.hus.englishapp.kuro.model.dto.response.AuthResponse;
import com.hus.englishapp.kuro.repository.UserRepository;
import com.hus.englishapp.kuro.service.ExcelService;
import com.hus.englishapp.kuro.service.PasswordResetService;
import com.hus.englishapp.kuro.service.UserService;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import com.hus.englishapp.kuro.service.JwtService;
import io.jsonwebtoken.Jwts;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.*;

//@Controller
@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MessageTemplate messageTemplate;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }
//
//    @GetMapping("/about")
//    public String about() {
//        return "about";
//    }
//

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUser(user));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.create(userRequestDto);
        return ResponseEntity.ok().body(userResponseDto);
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @GetMapping("/user/userProfile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String userProfile() {
        return "Welcome to User Profile";
    }

    @GetMapping("/admin/adminProfile")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminProfile() {
        return "Welcome to Admin Profile";
    }

    @GetMapping("/profile")
    public User getInfoUser() {
        return userService.currUser();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        String oldRefreshToken = request.getRefreshToken();

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

        // Trả về cả Access Token & Refresh Token
        Map<String, String> tokens = new HashMap<>();
        tokens.put("token", newAccessToken);
        tokens.put("refresh_token", newRefreshToken);

        return ResponseEntity.ok(tokens);
    }


    @PostMapping("/generateToken")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
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

                // Trả về cả Access Token & Refresh Token
                Map<String, String> tokens = new HashMap<>();
                tokens.put("token", accessToken);
                tokens.put("refresh_token", refreshToken);
                return ResponseEntity.ok(tokens);
            }
        } catch (BadCredentialsException ex) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message",
                    messageTemplate.message("error.UserController.passwordIncorrect")));
        } catch (UsernameNotFoundException ex) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message",
                    messageTemplate.message("error.UserController.usernameNotExist")));
        } catch (Exception ex) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message",
                    messageTemplate.message("error.UserController.errorAuthen") + ex.getMessage()));
        }
//        Map<String, String> response = new HashMap<>();
//        response.put("token", token);
//        return response;

//        if (authentication.isAuthenticated()) {
//            return jwtService.generateToken(authRequest.getUsername());
//        } else {
//            throw new UsernameNotFoundException("Invalid user request!");
//        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", messageTemplate.message("error.UserController.errorAuthen")));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        String refreshToken = userService.extractRefreshToken(token);
        if (refreshToken != null) {
            userService.deleteRefreshToken(refreshToken);
        }
        return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        passwordResetService.sendResetEmail(email);
        return ResponseEntity.ok("Vui lòng kiểm tra email để đặt lại mật khẩu. Please check your email to reset password");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        boolean success = passwordResetService.resetPassword(token, newPassword);
        return success ? ResponseEntity.ok("Mật khẩu đã được đặt lại thành công. Password had been reset success") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token không hợp lệ. Invalid token");
    }
}
