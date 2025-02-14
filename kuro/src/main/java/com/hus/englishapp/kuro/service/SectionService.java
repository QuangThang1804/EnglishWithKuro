package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.model.Ques;
import com.hus.englishapp.kuro.model.Section;
import com.hus.englishapp.kuro.model.dto.SectionRequestDto;
import com.hus.englishapp.kuro.model.dto.SectionResponseContentDto;
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
import java.util.UUID;

@Service
public class SectionService {
    @Autowired
    private SectionRepository sectionRepository;

    public Page<SectionResponseContentDto> findAll(Pageable pageable) {
        Page<Section> sectionList = sectionRepository.findAll(pageable);
        List<SectionResponseContentDto> sectionResponseDetailDtos = new ArrayList<>();
        for (Section section : sectionList) {
            SectionResponseContentDto sectionResponseDetailDto = new SectionResponseContentDto();
            sectionResponseDetailDto.setId(section.getId());
            sectionResponseDetailDto.setSectionKind(section.getSectionKind());
            sectionResponseDetailDto.setSectionName(section.getSectionName());
            sectionResponseDetailDtos.add(sectionResponseDetailDto);
        }
        return new PageImpl<>(sectionResponseDetailDtos);
    }

    public Page<SectionResponseContentDto> search(Pageable pageable, String sectionKind, String sectionName) {
        Page<SectionResponseContentDto> sectionList = sectionRepository.search(pageable, sectionKind, sectionName);
        List<SectionResponseContentDto> sectionResponseDetailDtos = new ArrayList<>();
        for (SectionResponseContentDto section : sectionList) {
            SectionResponseContentDto sectionResponseDetailDto = new SectionResponseContentDto();
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
        } else {
            section.setId(UUID.randomUUID().toString());
        }
        section.setSectionKind(sectionRequestDto.getSectionKind());
        section.setSectionName(sectionRequestDto.getSectionName());
        section.setSectionContent(sectionRequestDto.getSectionContent());
        return sectionRepository.save(section);
    }

    public void deleteById(String id) {
        sectionRepository.deleteById(id);
    }



    public Page<Section> changeStr(Pageable pageable) {
        Page<Section> sectionList = sectionRepository.findAll(pageable);
        for (Section section: sectionList) {
            String contentSection = section.getSectionContent();
            contentSection = contentSection.replace(",", ";");
            String [] listSentences = contentSection.split(";");
            List<Ques> quesList = new ArrayList<>();
            for (String str:listSentences) {
                String[] question = str.split(":");
                Ques ques = new Ques();
                ques.setS1LanguageWords(question[0]);
                ques.setS2LanguageWords(question[1]);
                quesList.add(ques);
            }
            section.setSectionContent(contentSection);
            sectionRepository.save(section);
        }

        return sectionList;
    }
}
