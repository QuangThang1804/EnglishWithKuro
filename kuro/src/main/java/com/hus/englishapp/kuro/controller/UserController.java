package com.hus.englishapp.kuro.controller;

import com.hus.englishapp.kuro.config.MessageTemplate;
import com.hus.englishapp.kuro.model.AuthRequest;
import com.hus.englishapp.kuro.model.User;
import com.hus.englishapp.kuro.model.dto.UserRequestDto;
import com.hus.englishapp.kuro.model.dto.UserResponseDto;
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
    public String registerUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/login";
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

    @PostMapping("/generateToken")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(authRequest.getUsername());
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
}
