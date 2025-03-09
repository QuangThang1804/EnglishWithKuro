package com.hus.englishapp.kuro.model.dto;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.hus.englishapp.kuro.model.User}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    private String id;
    String username;
    String fullname;
    String password;
    String email;
    String roles;
    String resetToken;
}