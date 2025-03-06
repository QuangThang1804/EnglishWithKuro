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
@Entity(name = "MATCH_CARD")
public class MatchCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "SECTION_ID")
    private String sectionId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "TIME_TEST")
    private double timeTest;
}
