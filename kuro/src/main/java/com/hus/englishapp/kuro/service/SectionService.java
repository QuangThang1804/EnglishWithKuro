package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.model.Section;
import com.hus.englishapp.kuro.model.dto.SectionRequestDto;
import com.hus.englishapp.kuro.model.dto.SectionResponseDetailDto;
import com.hus.englishapp.kuro.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SectionService {
    @Autowired
    private SectionRepository sectionRepository;

    public Page<SectionResponseDetailDto> findAll(Pageable pageable) {
        Page<Section> sectionList = sectionRepository.findAll(pageable);
        List<SectionResponseDetailDto> sectionResponseDetailDtos = new ArrayList<>();
        for (Section section : sectionList) {
            SectionResponseDetailDto sectionResponseDetailDto = new SectionResponseDetailDto();
            sectionResponseDetailDto.setId(section.getId());
            sectionResponseDetailDto.setSectionKind(section.getSectionKind());
            sectionResponseDetailDto.setSectionName(section.getSectionName());
            sectionResponseDetailDtos.add(sectionResponseDetailDto);
        }
        return new PageImpl<>(sectionResponseDetailDtos);
    }

    public Section update(SectionRequestDto sectionRequestDto) {
        Section section = new Section();
        if (sectionRequestDto.getId() != null) {
            Optional<Section> sectionInDb = sectionRepository.findById(sectionRequestDto.getId());
            if (sectionInDb.isPresent()) {
                section = sectionInDb.get();
            }
        }
        section.setSectionKind(sectionRequestDto.getSectionKind());
        section.setSectionName(sectionRequestDto.getSectionName());
        section.setSectionContent(sectionRequestDto.getSectionContent());
        return sectionRepository.save(section);
    }

    public void deleteById(String id) {
        sectionRepository.deleteById(id);
    }
}
