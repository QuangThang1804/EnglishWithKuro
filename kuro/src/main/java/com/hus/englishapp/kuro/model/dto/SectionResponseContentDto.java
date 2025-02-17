package com.hus.englishapp.kuro.model.dto;

import com.hus.englishapp.kuro.model.Ques;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectionResponseContentDto {
    private String id;
    private String sectionKind;
    private String sectionName;
    private String sectionContent;
    private List<Ques> sectionQuesList;
}
