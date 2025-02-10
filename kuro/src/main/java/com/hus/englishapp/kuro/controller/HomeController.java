package com.hus.englishapp.kuro.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

//    @GetMapping("/")
//    public String index(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null && authentication.isAuthenticated()
//                && !authentication.getPrincipal().equals("anonymousUser")) {
//            String userName = authentication.getName();
//            model.addAttribute("loggedIn", true);
//            model.addAttribute("userName", userName);
//        } else {
//            model.addAttribute("loggedIn", false);
//        }
//
//        return "index";
//    }

//    @GetMapping("/")
//    public String index(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null && authentication.isAuthenticated()
//                && !authentication.getPrincipal().equals("anonymousUser")) {
//            String userName = authentication.getName();
//            model.addAttribute("loggedIn", true);
//            model.addAttribute("userName", userName);
//        } else {
//            model.addAttribute("loggedIn", false);
//        }
//
//        return "index";
//    }
}
