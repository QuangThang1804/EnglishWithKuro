package com.hus.englishapp.kuro.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailAuthenticationRequest {
    @NotBlank(message = "Email là bắt buộc")
    private String email;
}
