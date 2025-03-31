package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.exception.AppException;
import com.hus.englishapp.kuro.model.User;
import com.hus.englishapp.kuro.model.dto.response.PasswordConfirmRequest;
import com.hus.englishapp.kuro.repository.UserRepository;
import com.hus.englishapp.kuro.util.Constants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class PasswordResetService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;
    @Autowired
    private SendEmailService sendEmailService;

    @Transactional
    public void sendResetEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmailAndDifferentProvider(email, Constants.TYPE_ACCOUNT.NORMAL);
        if (userOptional.isPresent()) {
            // Create code random (6 number)
            Random random = new Random();
            String code = String.valueOf((100000 + random.nextInt(900000)));
            String token = encoder.encode(code);

            User user = userOptional.get();
            user.setResetToken(token);
            userRepository.save(user);

            // Gửi email với link reset
            String subject = "Mã bảo mật Kolin của bạn";
            String body = "<html><body>"
                    + "<p>Xin chào <b>" + user.getFullname() + "</b>,</p>"
                    + "<p>Mã bảo mật của bạn là: <b style='color:blue;'>" + code + "</b></p>"
                    + "<p>Để xác nhận danh tính của bạn trên Kolin, chúng tôi cần xác minh " +
                    "địa chỉ email của bạn. Hãy dán mã này vào trình duyệt. Đây là mã dùng một lần.</b></p>" +
                    "<p>Cảm ơn bạn! </b></p>" +
                    "<p>Đội ngũ bảo mật của Kolin </b></p>"
                    + "</body></html>";

            sendEmailService.sendEmail(email, subject, body);
        } else {
            throw new AppException("Không tìm thấy tài khoản người dùng", HttpStatus.NOT_FOUND);
        }
    }

    public boolean isValidCodeResetPassword(String email, String code) {
        Optional<User> userOptional = userRepository.findByEmailAndDifferentProvider(email, Constants.TYPE_ACCOUNT.NORMAL);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return encoder.matches(code, user.getResetToken());
        }

        return false;
    }

    @Transactional
    public boolean resetPassword(String email, String code, String newPassword) {
        Optional<User> userOptional =
                userRepository.findByEmailAndDifferentProvider(email, Constants.TYPE_ACCOUNT.NORMAL);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Kiểm tra xem đúng code xác thực không
            if (encoder.matches(code, user.getResetToken())) {
                if (isValidPassword(newPassword, user.getPassword())) {
                    throw new AppException("Mật khẩu hiện bạn đang sử dụng, hãy đổi mật khẩu khác nhé!", HttpStatus.NOT_ACCEPTABLE);
                }

                user.setPassword(encoder.encode(newPassword));
                user.setResetToken(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean changePassword(String oldPassword, String newPassword) {
        // 1. Xác thực người dùng hiện tại
        User currentUser = userService.currUser();
        if (currentUser == null) {
            throw new AppException("Không tìm thấy tài khoản người dùng hiện tại", HttpStatus.UNAUTHORIZED);
        }

        // 2. Tìm user
        Optional<User> userOptional = userRepository.findById(currentUser.getId());
        User user = userOptional.get();

        // 3. So sánh mật khẩu cũ (sử dụng matches thay vì encode)
        if (!encoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        // 4. So sánh mật khẩu mới (sử dụng matches thay vì encode)
        if (encoder.matches(newPassword, user.getPassword())) {
            throw new AppException("Mật khẩu đã trùng với mật khẩu cũ", HttpStatus.BAD_REQUEST);
        }

        // 5. Cập nhật mật khẩu mới
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }

    public boolean isValidPassword(String password, String confirmPassword) {
        if (encoder.matches(password, confirmPassword)) {
            return true;
        }
        return false;
    }

    public boolean confirmUser(PasswordConfirmRequest passwordRequest) throws GeneralSecurityException, IOException {
        boolean result = false;
        User currentUser = userService.currUser();
        if (currentUser == null) {
            throw new AppException("Không tìm thấy tài khoản người dùng hiện tại", HttpStatus.UNAUTHORIZED);
        }

        // 1. Tìm user
        Optional<User> userOptional = userRepository.findById(currentUser.getId());
        User user = userOptional.get();

        // 2. Kiểm tra xác thực loại đăng nhập
        if (Objects.equals(passwordRequest.getType(), Constants.TYPE_ACCOUNT.GOOGLE)) {
            String email = authService.verifyGoogleToken(passwordRequest.getGoogleToken(), Constants.TYPE_ACCOUNT.GOOGLE_TYPE.CONFIRM_GOOGLE);
            if (Objects.equals(email, user.getEmail())) {
                result = true;
            }
        } else if (Objects.equals(passwordRequest.getType(), Constants.TYPE_ACCOUNT.NORMAL)) {
            result = isValidPassword(passwordRequest.getPassword(), user.getPassword());
        }

        return result;
    }

}

