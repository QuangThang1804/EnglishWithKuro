package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.model.EmailAuthentication;
import com.hus.englishapp.kuro.model.dto.EmailAuthenticationRequest;
import com.hus.englishapp.kuro.repository.EmailAuthenticationRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailAuthenticationService {
    private final EmailAuthenticationRepository emailAuthenticationRepository;
    private final PasswordEncoder encode;
    private final SendEmailService sendEmailService;

    @Transactional(readOnly = true)
    public EmailAuthentication findByEmail(String email) {
        return emailAuthenticationRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public EmailAuthentication save(EmailAuthenticationRequest request) {
        // Create code random (6 number)
        Random random = new Random();
        String code = String.valueOf((100000 + random.nextInt(900000)));

        // Mã hóa code
        String token = encode.encode(code);
        String email = request.getEmail();

        // Gửi email xác thực
        String subject = "Mã bảo mật Kolin của bạn";
        String body = "<html><body>"
                + "<p>Xin chào bạn,</p>"
                + "<p>Mã xác thực của bạn là: <b style='color:blue;'>" + code + "</b></p>"
                + "<p>Hãy nhập mã này để hoàn tất đăng ký.</p>"
                + "<p>Cảm ơn!</p>"
                + "<p>Đội ngũ bảo mật của Kolin</p>"
                + "</body></html>";
        sendEmailService.sendEmail(email, subject, body);

        // Tìm email
        EmailAuthentication emailAuthentication = findByEmail(email);

        if (emailAuthentication != null) {
            emailAuthentication.setCodeConfirm(token);
        } else {
            emailAuthentication = new EmailAuthentication();
            emailAuthentication.setEmail(email);
            emailAuthentication.setCodeConfirm(token);
        }
        return emailAuthenticationRepository.save(emailAuthentication);
    }

}
