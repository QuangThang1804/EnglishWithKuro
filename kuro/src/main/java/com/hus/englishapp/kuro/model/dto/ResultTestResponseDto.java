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
public class ResultTestResponseDto {
    private Integer id;
    private String sectionId;
    private String userId;
    private String wrongQuesId;
    private List<Ques> listQues;
}
