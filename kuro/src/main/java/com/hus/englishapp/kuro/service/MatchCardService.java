package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.exception.AppException;
import com.hus.englishapp.kuro.model.MatchCard;
import com.hus.englishapp.kuro.model.Section;
import com.hus.englishapp.kuro.model.SectionContent;
import com.hus.englishapp.kuro.model.User;
import com.hus.englishapp.kuro.model.dto.MatchCardRequest;
import com.hus.englishapp.kuro.model.dto.MatchCardResponse;
import com.hus.englishapp.kuro.repository.MatchCardRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MatchCardService {
    @Autowired
    private MatchCardRepository matchCardRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public MatchCard create(MatchCardRequest matchCardRequest) {
        try {
            MatchCard matchCard = new MatchCard();
            String userId = userService.currUser().getId();

            if (userId == null) {
                throw new AppException("Không tìm thấy tài khoản người dùng hiện tại", HttpStatus.BAD_REQUEST);
            }

            Optional<MatchCard> matchCardInDb = matchCardRepository.findBySectionIdAndUserId(userId, matchCardRequest.getSectionId());
            if (matchCardInDb.isPresent()) {
                matchCard = matchCardInDb.get();
                if (matchCard.getTimeTest() > matchCardRequest.getTimeTest() || matchCard.getTimeTest() == 0) {
                    matchCard.setTimeTest(matchCardRequest.getTimeTest());
                }
                return matchCardRepository.save(matchCard);
            }

            matchCard.setUserId(userId);
            matchCard.setSectionId(matchCardRequest.getSectionId());
            matchCard.setTimeTest(matchCardRequest.getTimeTest());
            return matchCardRepository.save(matchCard);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Page<MatchCardResponse> findAllBySectionId(String sectionId, Pageable pageable) {
        List<MatchCardResponse> listResults = matchCardRepository.findAllBySection(sectionId);
        return new PageImpl<>(listResults, pageable, listResults.size());
    }
}
