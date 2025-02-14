package com.hus.englishapp.kuro.repository;

import com.hus.englishapp.kuro.model.Section;
import com.hus.englishapp.kuro.model.dto.SectionRequestDto;
import com.hus.englishapp.kuro.model.dto.SectionResponseContentDto;
import com.hus.englishapp.kuro.model.dto.SectionResponseDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SectionRepositoryCustom {
    Page<SectionResponseContentDto> search(Pageable pageable, String kind, String name);
}
