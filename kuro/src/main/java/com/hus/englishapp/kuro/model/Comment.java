package com.hus.englishapp.kuro.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "COMMENT")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "SECTION_ID")
    private String sectionId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "COMMENT")
    private String comment;
}
