package com.hus.englishapp.kuro.controller;

import com.hus.englishapp.kuro.model.EmailAuthentication;
import com.hus.englishapp.kuro.model.dto.EmailAuthenticationRequest;
import com.hus.englishapp.kuro.service.EmailAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email-authentication")
@RequiredArgsConstructor
public class EmailAuthenticationController {
    private final EmailAuthenticationService emailAuthenticationService;

    @PostMapping("save")
    public ResponseEntity<?> saveEmailAuthentication(@RequestBody EmailAuthenticationRequest request) {
        EmailAuthentication emailAuthentication = emailAuthenticationService.save(request);
        return ResponseEntity.ok(emailAuthentication);
    }
}
