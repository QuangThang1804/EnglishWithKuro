package com.hus.englishapp.kuro.controller;

import com.hus.englishapp.kuro.model.AuthRequest;
import com.hus.englishapp.kuro.model.User;
import com.hus.englishapp.kuro.model.dto.SocialLoginRequest;
import com.hus.englishapp.kuro.model.dto.UserRequestDto;
import com.hus.englishapp.kuro.model.dto.UserResponseDto;
import com.hus.englishapp.kuro.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.*;

//@Controller
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final AuthService authService;
    private final JwtService jwtService;

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
    public ResponseEntity<?> refresh(@CookieValue("refresh_token") String oldRefreshToken) {
        return authService.refreshToken(oldRefreshToken);
    }


    @PostMapping("/generateToken")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        return authService.authenticateAndGenerateToken(authRequest);
    }

    @PostMapping("social-login")
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginRequest request,
                                         HttpServletResponse response
    ) {
        try {
            // Xác thực token Google và lấy email
            String email = authService.verifyGoogleToken(request.getToken());

            // Tạo tokens
            String accessToken = jwtService.generateToken(email);
            String refreshToken = jwtService.generateRefreshToken(email);

            // Lưu refresh token vào database
            authService.saveRefreshToken(email, refreshToken);

            // Set refresh token vào cookie
            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true)
                    .secure(true)  // Chỉ gửi qua HTTPS
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 ngày
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            // Trả về access token trong body
            return ResponseEntity.ok(Collections.singletonMap("token", accessToken));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Google login failed: " + e.getMessage());
        }
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
