package com.hus.englishapp.kuro.controller;

import com.hus.englishapp.kuro.config.MessageTemplate;
import com.hus.englishapp.kuro.model.AuthRequest;
import com.hus.englishapp.kuro.model.User;
import com.hus.englishapp.kuro.model.dto.UserDto;
import com.hus.englishapp.kuro.model.dto.UserRequestDto;
import com.hus.englishapp.kuro.model.dto.UserResponseDto;
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

    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody User userInfo) {
        return userService.addUser(userInfo);
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
        Authentication ath = SecurityContextHolder.getContext().getAuthentication();
        if (ath != null && ath.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) ath.getPrincipal();
            return userService.findUserByUsername(userDetails.getUsername());
        }
        return null;
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

                // Tạo token từ UserDetails (có thể lấy thêm thông tin nếu cần)
                String token = jwtService.generateToken(userDetails.getUsername());
                return ResponseEntity.ok(Collections.singletonMap("token", token));
            }
        } catch (BadCredentialsException ex) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", messageTemplate.message("error.UserController.passwordIncorrect")));
        } catch (UsernameNotFoundException ex) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", messageTemplate.message("error.UserController.usernameNotExist")));
        } catch (Exception ex) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", messageTemplate.message("error.UserController.errorAuthen") + ex.getMessage()));
        }
//        Map<String, String> response = new HashMap<>();
//        response.put("token", token);
//        return response;

//        if (authentication.isAuthenticated()) {
//            return jwtService.generateToken(authRequest.getUsername());
//        } else {
//            throw new UsernameNotFoundException("Invalid user request!");
//        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", messageTemplate.message("error.UserController.errorAuthen") ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        passwordResetService.sendResetEmail(email);
        return ResponseEntity.ok("Vui lòng kiểm tra email để đặt lại mật khẩu.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        boolean success = passwordResetService.resetPassword(token, newPassword);
        return success ? ResponseEntity.ok("Mật khẩu đã được đặt lại thành công.") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token không hợp lệ.");
    }
}
