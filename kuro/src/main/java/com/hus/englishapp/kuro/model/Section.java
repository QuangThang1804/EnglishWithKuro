package com.hus.englishapp.kuro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SECTION_SYS")
public class Section {
    @Id
    private String id;

    @Column(name = "SECTION_KIND")
    private String sectionKind;

    @Column(name = "SECTION_NAME")
    private String sectionName;

    @Column(name = "SECTION_CONTENT")
    private String sectionContent;
}
