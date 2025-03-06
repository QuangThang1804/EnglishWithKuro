package com.hus.englishapp.kuro.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchCardResponse {
    private Integer id;
    private String sectionId;
    private String name;
    private double timeTest;
}
