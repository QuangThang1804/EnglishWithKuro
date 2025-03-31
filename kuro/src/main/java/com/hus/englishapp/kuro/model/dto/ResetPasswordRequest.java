package com.hus.englishapp.kuro.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordRequest {
    private String email;
    private String code;
    private String newPassword;
}
