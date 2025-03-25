package com.hus.englishapp.kuro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hus.englishapp.kuro.config.MessageTemplate;
import com.hus.englishapp.kuro.model.MatchCard;
import com.hus.englishapp.kuro.model.dto.MatchCardRequest;
import com.hus.englishapp.kuro.model.dto.MatchCardResponse;
import com.hus.englishapp.kuro.model.dto.ResponseDTO;
import com.hus.englishapp.kuro.service.MatchCardService;
import com.hus.englishapp.kuro.util.Constants;
import com.hus.englishapp.kuro.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matchCard")
@CrossOrigin("http://localhost:3000")
@Validated
@Slf4j
public class MatchCardController {
    @Autowired
    private MatchCardService matchCardService;

    @Autowired
    private MessageTemplate messageTemplate;

    @GetMapping("/get-time")
    public ResponseEntity<?> findAllBySection(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") Integer size,
            @RequestParam(name = "sort", required = false) List<String> sorts,
            @RequestParam(name = "sectionId", required = true) String sectionId
    ) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Page<MatchCardResponse> pageMatchCards = matchCardService.findAllBySectionId(sectionId, PagingUtil.buildPageable(page, size, sorts));

            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.SUCCESS)
                    .data(mapper.valueToTree(pageMatchCards.getContent()))
                    .meta(ResponseDTO.Meta.builder()
                            .page(page + 1)
                            .total(pageMatchCards.getSize())
                            .pageOfNumber(pageMatchCards.getTotalPages())
                            .build())
                    .msg("SUCCESS")
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.FAILURE)
                    .data(null)
                    .msg("FAIL")
                    .build();
            return ResponseEntity.ok(responseDTO);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<?> create(@Validated @RequestBody MatchCardRequest matchCardRequest) {

        MatchCard matchCard = matchCardService.create(matchCardRequest);
        return ResponseEntity.ok(matchCard);
    }
}
