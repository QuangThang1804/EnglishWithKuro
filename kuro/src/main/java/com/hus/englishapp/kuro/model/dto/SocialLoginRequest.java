package com.hus.englishapp.kuro.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialLoginRequest {
    private String loginType;
    private String token;
}
