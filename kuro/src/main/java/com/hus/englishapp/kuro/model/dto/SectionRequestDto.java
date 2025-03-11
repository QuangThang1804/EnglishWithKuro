package com.hus.englishapp.kuro.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectionRequestDto {
    private String id;
    private String sectionKind;
    private String sectionName;
    private String sectionContent;
    private List<String> sectionQuesions;
    private Map<String, String> sectionQuesMap;
}
