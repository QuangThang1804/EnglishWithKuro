package com.hus.englishapp.kuro.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "EMAIL_AUTHENTICATION")
public class EmailAuthentication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    @NotBlank(message = "Email confirm là bắt buộc")
    private String email;

    @Column(name = "codeConfirm")
    @NotBlank(message = "Code confirm là bắt buộc")
    private String codeConfirm;
}
