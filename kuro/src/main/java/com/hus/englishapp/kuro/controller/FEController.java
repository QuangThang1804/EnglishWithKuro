package com.hus.englishapp.kuro.controller;

import com.hus.englishapp.kuro.model.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class FEController {
    @Autowired
    private UserController userController;

//    @GetMapping("/")
//    public String home() {
//        return "index";
//    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String userName = authentication.getName();  // Lấy tên người dùng đã đăng nhập
            model.addAttribute("userName", userName);
        }
        return "index";  // Trả về view index.html
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/courses")
    public String coursePage() {
        return "courses";
    }
//    @PostMapping("/doLogin")
//    public String login(@RequestBody AuthRequest authRequest) {
//        String token = userController.authenticateAndGetToken(authRequest);
//        if (token != null) {
//            return "redirect:/";
//        }
//        return "login";
//    }
}
