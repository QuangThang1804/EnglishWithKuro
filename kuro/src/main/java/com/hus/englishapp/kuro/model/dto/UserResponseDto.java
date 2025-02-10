package com.hus.englishapp.kuro.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private String id;
    private String username;
    private String password;
    private String email;
}
