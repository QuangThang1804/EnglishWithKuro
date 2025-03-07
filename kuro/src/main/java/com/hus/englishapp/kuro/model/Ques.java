package com.hus.englishapp.kuro.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Ques {
    private String quesId;
    private String s1LanguageWords;
    private String s2LanguageWords;
}
