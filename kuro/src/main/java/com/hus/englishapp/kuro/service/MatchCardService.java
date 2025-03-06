package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.model.MatchCard;
import com.hus.englishapp.kuro.model.Section;
import com.hus.englishapp.kuro.model.SectionContent;
import com.hus.englishapp.kuro.model.dto.MatchCardRequest;
import com.hus.englishapp.kuro.model.dto.MatchCardResponse;
import com.hus.englishapp.kuro.repository.MatchCardRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MatchCardService {
    @Autowired
    private MatchCardRepository matchCardRepository;

    public MatchCard create(MatchCardRequest matchCardRequest) {
        MatchCard matchCard = new MatchCard();
        if (matchCardRequest.getId() != null) {
            Optional<MatchCard> matchCardInDb = matchCardRepository.findById(matchCardRequest.getId());
            if (matchCardInDb.isPresent()) {
                matchCard = matchCardInDb.get();
            }
        }

        matchCard.setSectionId(matchCardRequest.getSectionId());
        matchCard.setUserId(matchCardRequest.getUserId());
        matchCard.setTimeTest(matchCardRequest.getTimeTest());
        return matchCardRepository.save(matchCard);
    }

    public Page<MatchCardResponse> findAllBySectionId(String sectionId, Pageable pageable) {
        List<MatchCardResponse> listResults = matchCardRepository.findAllBySection(sectionId);
        return new PageImpl<>(listResults, pageable, listResults.size());
    }
}
