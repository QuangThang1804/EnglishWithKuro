package com.hus.englishapp.kuro.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class SectionResponseDetailDto {
    private String id;
    private String sectionKind;
    private String sectionName;

    public SectionResponseDetailDto(String id, String sectionKind, String sectionName) {
        this.id = id;
        this.sectionKind = sectionKind;
        this.sectionName = sectionName;
    }
}
