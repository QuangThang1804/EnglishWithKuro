package com.hus.englishapp.kuro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "SECTION_CONTENT")
public class SectionContent {
    @Id
    private String id;

    @Column(name = "SECTION_ID")
    private String sectionId;

    @Column(name = "SECTION_KIND")
    private String sectionKind;

    @Column(name = "SECTION_NAME")
    private String sectionName;

    @Column(name = "SECTION_QUES")
    private String sectionQues;

    @Column(name = "QUESTION")
    private String question;

    @Column(name = "ANSWER")
    private String answer;
}
