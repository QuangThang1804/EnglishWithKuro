package com.hus.englishapp.kuro.controller;

import com.hus.englishapp.kuro.model.dto.*;
import com.hus.englishapp.kuro.model.User;
import com.hus.englishapp.kuro.model.dto.response.PasswordConfirmRequest;
import com.hus.englishapp.kuro.service.*;
import com.hus.englishapp.kuro.util.Constants;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.io.IOException;
import java.security.GeneralSecurityException;
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
    public ResponseEntity<User> registerUser(@RequestBody UserRequestDto user) {
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
    @Transactional
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginRequest request,
                                         HttpServletResponse response
    ) {
        try {
            // Xác thực token Google và lấy email
            String email = authService.verifyGoogleToken(request.getToken(), Constants.TYPE_ACCOUNT.GOOGLE_TYPE.LOGIN_GOOGLE);

            // Tạo tokens
            String accessToken = jwtService.generateToken(email);
            String refreshToken = jwtService.generateRefreshToken(email);

            // Lưu refresh token vào database
            authService.saveRefreshToken(email, refreshToken, Constants.TYPE_ACCOUNT.GOOGLE);

            // Set refresh token vào cookie
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

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Google login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue("refresh_token") String oldRefreshToken,
            HttpServletResponse response
    ) {
        // 1. Revoke refresh token trong DB (nếu tồn tại)
        if (oldRefreshToken != null) {
            authService.logout(oldRefreshToken);
        }

        // 2. Xóa cookie refresh token ở client
        ResponseCookie deleteRefreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true) // Chỉ hoạt động với HTTPS
                .path("/") // Khớp với path refresh token
                .maxAge(0) // Xóa cookie ngay lập tức
                .sameSite("Strict") // Chống CSRF
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString());

        return ResponseEntity.ok()
                .body(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        passwordResetService.sendResetEmail(email);
        return ResponseEntity.ok(Map.of("message", "Vui lòng kiểm tra email để đặt lại mật khẩu"));
    }

    @PostMapping("confirm-code-reset-password")
    public ResponseEntity<?> confirmCodeResetPassword(@RequestBody ResetPasswordRequest request) {
        boolean success = passwordResetService.isValidCodeResetPassword(
                request.getEmail(),
                request.getCode()
        );

        return success ? ResponseEntity.ok(Map.of("message", "Xác thực thành công"))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Xác thực không thành công"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean success = passwordResetService.resetPassword(
                request.getEmail(),
                request.getCode(),
                request.getNewPassword()
        );
        return success ? ResponseEntity.ok(Map.of("message", "Mật khẩu đã được đặt lại thành công.")) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Lỗi xác thực. Hãy thử lại nhé!."));
    }

    @PostMapping("/confirm-user")
    public ResponseEntity<Map<String, String>> confirmUser(@RequestBody PasswordConfirmRequest passwordRequest) throws GeneralSecurityException, IOException {
        boolean success = passwordResetService.confirmUser(passwordRequest);

        Map<String, String> response = new HashMap<>();
        response.put("message", success ?
                "Xác thực thành công." :
                "Xác thực không thành công.");

        return success ? ResponseEntity.ok(response) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody PasswordRequest passwordRequest) {
        boolean success = passwordResetService.changePassword(passwordRequest.getOldPassword(), passwordRequest.getNewPassword());
        Map<String, String> response = new HashMap<>();
        response.put("message", success ?
                "Mật khẩu đã được đổi thành công." :
                "Mật khẩu cũ không khớp.");

        return success ? ResponseEntity.ok(response) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/update-info-user")
    public ResponseEntity<?> updateInfoUser(@RequestBody UserRequestDto requestDto) {
        User result = userService.updateUser(requestDto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestBody UserRequestDto requestDto) {
        userService.deletedUser(requestDto);
        return ResponseEntity.ok(Map.of("message", "Xóa tài khoản thành công"));
    }


}
