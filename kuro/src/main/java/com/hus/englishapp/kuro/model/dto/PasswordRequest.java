package com.hus.englishapp.kuro.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordRequest {
    private String oldPassword;
    private String newPassword;
}
