package com.hus.englishapp.kuro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "SECTION_SYS")
public class Section {
    @Id
    private String id;

    private String sectionKind;

    private String sectionName;

    private String sectionContent;
}
