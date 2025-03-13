package com.hus.englishapp.kuro.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;

@Getter
@Setter
public class SectionContentDto implements Serializable {
    String sectionKind;
    String sectionName;
}