package com.hus.englishapp.kuro.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordConfirmRequest {
    private String googleToken;
    private String password;
    private String type;
}
