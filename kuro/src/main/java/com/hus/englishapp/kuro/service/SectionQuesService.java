package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.model.SectionContent;
import com.hus.englishapp.kuro.repository.SectionContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SectionQuesService {
    @Autowired
    private SectionContentRepository sectionContentRepository;

    public SectionContent findById(String id) {
        Optional<SectionContent> sectionContent = sectionContentRepository.findById(id);
        return sectionContent.orElse(null);
    }

}
