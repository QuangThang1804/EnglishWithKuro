package com.hus.englishapp.kuro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "User")
public class User {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    private String roles;

    private String resetToken;

//    @Version
//    private Integer version;
}
