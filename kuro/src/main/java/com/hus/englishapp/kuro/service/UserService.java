package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.exception.AppException;
import com.hus.englishapp.kuro.model.User;
import com.hus.englishapp.kuro.model.dto.UserRequestDto;
import com.hus.englishapp.kuro.model.dto.UserResponseDto;
import com.hus.englishapp.kuro.repository.UserRepository;
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

import java.util.UUID;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = userRepository.findByUsername(username); // Assuming 'email' is used as username

        // Converting UserInfo to UserDetails
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional()
    public User saveUser(User user) {
        try {
            if (userRepository.checkAccountAvailable(user.getUsername(), user.getEmail()) > 0) {
                throw new AppException("Tài khoản đã tồn tại!", HttpStatus.BAD_REQUEST);
            }
            user.setId(UUID.randomUUID().toString());
            user.setPassword(encoder.encode(user.getPassword()));
            return userRepository.save(user);
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

    public void deleteRefreshToken(String refreshToken) {
        userRepository.deleteById(refreshToken);
    }

    public String extractRefreshToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
