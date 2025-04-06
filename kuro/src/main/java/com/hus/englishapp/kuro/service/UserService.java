package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.exception.AppException;
import com.hus.englishapp.kuro.model.EmailAuthentication;
import com.hus.englishapp.kuro.model.User;
import com.hus.englishapp.kuro.model.dto.UserRequestDto;
import com.hus.englishapp.kuro.model.dto.UserResponseDto;
import com.hus.englishapp.kuro.repository.EmailAuthenticationRepository;
import com.hus.englishapp.kuro.repository.UserRepository;
import com.hus.englishapp.kuro.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private EmailAuthenticationService emailAuthenticationService;
    @Autowired
    private EmailAuthenticationRepository emailAuthenticationRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = userRepository.findByUsername(username); // Assuming 'email' is used as username

        // Converting UserInfo to UserDetails
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }


    public UserDetails loadUserByIdentifier(String identifier) {
        // Tìm user bằng username hoặc email
        Optional<User> userOpt = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier));

        User user = userOpt.orElseThrow(() ->
                new UsernameNotFoundException("User not found with identifier: " + identifier));

        // Converting UserInfo to UserDetails
        return userOpt.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
    }

    @Transactional
    public User saveUser(UserRequestDto userRequestDto) {
        if (userRepository.checkAccountAvailable(
                userRequestDto.getUsername(),
                userRequestDto.getEmail(),
                Constants.TYPE_ACCOUNT.NORMAL) > 0) {
            throw new AppException("Tài khoản đã tồn tại!", HttpStatus.BAD_REQUEST);
        }

        // Xác thực email
        EmailAuthentication emailAuth = emailAuthenticationService.findByEmail(userRequestDto.getEmail());
        if (emailAuth == null || !encoder.matches(userRequestDto.getCode(), emailAuth.getCodeConfirm())) {
            throw new AppException("Email không xác thực thành công, hãy thử lại nhé!", HttpStatus.NOT_ACCEPTABLE);
        }

        // Xóa mã xác thực thay vì cập nhật null
        emailAuthenticationRepository.delete(emailAuth);

        // Tạo user
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setFullname(userRequestDto.getFullname());
        user.setUsername(userRequestDto.getUsername());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(encoder.encode(userRequestDto.getPassword()));
        user.setRoles(userRequestDto.getRoles());
        user.setProvider(Constants.TYPE_ACCOUNT.NORMAL);

        return userRepository.save(user);
    }


    @Transactional()
    public User updateUser(UserRequestDto response) {
        try {
            User user = findUserByUsername(response.getUsername());
            if (user != null) {
                //user.setEmail(response.getEmail());
                user.setFullname(response.getFullname());
                return userRepository.save(user);
            }

            throw new AppException("Cập nhập thất bại", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional()
    public void deletedUser(UserRequestDto response) {
        try {
            User user = findUserByUsername(response.getUsername());
            if (user != null) {
                userRepository.delete(user);
                return;
            }

            throw new AppException("Xóa tài khoản thất bại", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public User currUser() {
        try {
            Authentication ath = SecurityContextHolder.getContext().getAuthentication();
            if (ath == null || !ath.isAuthenticated()) {
                logger.warn("No authenticated user found.");
                return null;
            }

            if (!(ath.getPrincipal() instanceof UserDetails)) {
                logger.warn("Principal is not an instance of UserDetails: " + ath.getPrincipal());
                return null;
            }

            UserDetails userDetails = (UserDetails) ath.getPrincipal();
            User user = findUserByUsername(userDetails.getUsername());

            if (user == null) {
                logger.warn("User not found with username: " + userDetails.getUsername());
            }

            return user;
        } catch (Exception e) {
            logger.error("Cannot get current user.", e);
            return null;
        }
    }


    public UserResponseDto create(UserRequestDto userRequestDto) {
        User newUser = User.builder()
                .username(userRequestDto.getUsername())
                .password(userRequestDto.getPassword())
                .email(userRequestDto.getEmail())
                .roles("ROLE_USER")
                .build();
        newUser = userRepository.save(newUser);
        return buildResult(newUser);
    }

    public UserResponseDto buildResult(User newUser) {
        return UserResponseDto.builder()
                .id(newUser.getId())
                .username(newUser.getUsername())
                .password(newUser.getPassword())
                .email(newUser.getEmail())
                .build();
    }

    public User findUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }
}
