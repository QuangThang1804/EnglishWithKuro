package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.model.User;
import com.hus.englishapp.kuro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            userRepository.save(user);

            // Gửi email với link reset
            String resetLink = "http://localhost:9090/reset-password?token=" + token;
            sendEmail(email, "Đặt lại mật khẩu", "Nhấp vào đây để đặt lại mật khẩu: " + resetLink);
        }
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(newPassword); // Cần mã hóa mật khẩu
            user.setResetToken(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}

