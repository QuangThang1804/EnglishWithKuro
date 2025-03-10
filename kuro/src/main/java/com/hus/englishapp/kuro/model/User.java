package com.hus.englishapp.kuro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "User")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @JdbcTypeCode(SqlTypes.VARCHAR) // Nếu cột ID lưu dưới dạng VARCHAR
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private UUID id;


    @Column(name = "username")
    private String username;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "roles")
    private String roles;

    private String resetToken;

//    @Version
//    private Integer version;
}
