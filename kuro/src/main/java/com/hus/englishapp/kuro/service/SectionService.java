package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.model.Ques;
import com.hus.englishapp.kuro.model.Section;
import com.hus.englishapp.kuro.model.SectionContent;
import com.hus.englishapp.kuro.model.dto.SectionRequestDto;
import com.hus.englishapp.kuro.model.dto.SectionResponseContentDto;
import com.hus.englishapp.kuro.model.dto.SectionResponseDetailDto;
import com.hus.englishapp.kuro.repository.SectionContentRepository;
import com.hus.englishapp.kuro.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final SectionContentRepository sectionContentRepository;

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

    public SectionResponseContentDto findById(String id) {
        Optional<Section> sectionInDb = sectionRepository.findById(id);
        SectionResponseContentDto sectionResponseContentDto = new SectionResponseContentDto();
        if (sectionInDb.isPresent()) {
            sectionResponseContentDto.setId(sectionInDb.get().getId());
            sectionResponseContentDto.setSectionKind(sectionInDb.get().getSectionKind());
            sectionResponseContentDto.setSectionName(sectionInDb.get().getSectionName());

            List<Ques> quesList = getQuesList(sectionInDb.get().getId(),
                    sectionInDb.get().getSectionKind(), sectionInDb.get().getSectionName());
            sectionResponseContentDto.setSectionQuesList(quesList);
        }
        return sectionResponseContentDto;
    }

    public List<Ques> getQuesList(String sectionId, String sectionKind, String sectionName) {
        List<SectionContent> sectionContentList = sectionContentRepository.findListSectionQues(sectionId, sectionKind, sectionName);
        List<Ques> quesList = new ArrayList<>();
        for (SectionContent sectionContent: sectionContentList) {
            Ques newQues = new Ques();
            newQues.setQuesId(sectionContent.getId());
            newQues.setS1LanguageWords(sectionContent.getQuestion());
            newQues.setS2LanguageWords(sectionContent.getAnswer());
            quesList.add(newQues);
        }

        return quesList;
    }


    public Page<SectionResponseDetailDto> searchAll(String name, String kind, Pageable pageable) {
        return sectionRepository.searchAll(name, kind, pageable);
    }


//    public Page<SectionResponseDetailDto> search(Pageable pageable, String sectionKind, String sectionName) {
//        Page<SectionResponseDetailDto> sectionList = sectionRepository.search(pageable, sectionKind, sectionName);
//        List<SectionResponseDetailDto> sectionResponseDetailDtos = new ArrayList<>();
//        for (SectionResponseDetailDto section : sectionList) {
//            SectionResponseDetailDto sectionResponseDetailDto = new SectionResponseDetailDto();
//            sectionResponseDetailDto.setId(section.getId());
//            sectionResponseDetailDto.setSectionKind(section.getSectionKind());
//            sectionResponseDetailDto.setSectionName(section.getSectionName());
//            sectionResponseDetailDtos.add(sectionResponseDetailDto);
//        }
//        return new PageImpl<>(sectionResponseDetailDtos);
//    }

    @Transactional
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
//        if (!sectionRequestDto.getSectionQuesions().isEmpty()) {
//            section.setSectionContent(sectionRequestDto.getSectionContent());
//            String [] listSentences = sectionRequestDto.getSectionContent().split(";");
//            for (String str:sectionRequestDto.getSectionQuesions()) {
//                SectionContent sectionContent = new SectionContent();
//                sectionContent.setId(UUID.randomUUID().toString());
//                sectionContent.setSectionId(section.getId());
//                sectionContent.setSectionKind(section.getSectionKind());
//                sectionContent.setSectionName(section.getSectionName());
//                sectionContent.setSectionQues(str.trim());
//                sectionContentRepository.save(sectionContent);
//            }
//        }

        if (!sectionRequestDto.getSectionQuesMap().isEmpty()) {
            sectionContentRepository.deleteBySectionNameAndSectionKind(section.getSectionName(), section.getSectionKind());
            for (Map.Entry<String, String> entry : sectionRequestDto.getSectionQuesMap().entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
                SectionContent sectionContent = new SectionContent();
                sectionContent.setId(UUID.randomUUID().toString());
                sectionContent.setSectionId(section.getId());
                sectionContent.setSectionKind(section.getSectionKind());
                sectionContent.setSectionName(section.getSectionName());
                sectionContent.setQuestion(entry.getKey().trim());
                sectionContent.setAnswer(entry.getValue().trim());
                sectionContentRepository.save(sectionContent);
            }
        }
        return sectionRepository.save(section);
    }

    @Transactional
    public void deleteById(String id) {
        sectionRepository.deleteById(id);
    }



//    public Page<Section> changeStr(Pageable pageable) {
//        Page<Section> sectionList = sectionRepository.findAll(pageable);
//        for (Section section: sectionList) {
//            String contentSection = section.getSectionContent();
//            contentSection = contentSection.replace(",", ";");
//            String [] listSentences = contentSection.split(";");
//            List<Ques> quesList = new ArrayList<>();
//            for (String str:listSentences) {
//                String[] question = str.split(":");
//                Ques ques = new Ques();
//                ques.setS1LanguageWords(question[0]);
//                ques.setS2LanguageWords(question[1]);
//                quesList.add(ques);
//            }
//            section.setSectionContent(contentSection);
//            sectionRepository.save(section);
//        }
//
//        return sectionList;
//    }
}
