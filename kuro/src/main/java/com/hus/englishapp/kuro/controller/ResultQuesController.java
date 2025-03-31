package com.hus.englishapp.kuro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hus.englishapp.kuro.config.MessageTemplate;
import com.hus.englishapp.kuro.model.ResultTest;
import com.hus.englishapp.kuro.model.dto.ResponseDTO;
import com.hus.englishapp.kuro.model.dto.ResultTestRequest;
import com.hus.englishapp.kuro.model.dto.ResultTestResponseDto;
import com.hus.englishapp.kuro.service.ResultTestService;
import com.hus.englishapp.kuro.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resultTest")
@Validated
@Slf4j

public class ResultQuesController {
    @Autowired
    private ResultTestService resultTestService;

    @Autowired
    private MessageTemplate messageTemplate;

    @GetMapping("/findAllBySectionAndUserId")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<?> findAllBySectionAndUserId(
            @RequestParam(name = "sectionId") String sectionId
    ) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ResultTestResponseDto resultTestResponseDto = resultTestService.findAllBySectionIdAndUserId(sectionId);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.SUCCESS)
                    .data(mapper.valueToTree(resultTestResponseDto))
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.SUCCESS)
                    .data(null)
                    .msg(messageTemplate.message("error.ResultTest.findAllFail"))
                    .build();
            return ResponseEntity.ok(responseDTO);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<?> create(@Validated @RequestBody ResultTestRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ResultTest> listResultTest = resultTestService.create(request.getSectionId(), request.getWrongQuesId());
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.SUCCESS)
                    .data(mapper.valueToTree(listResultTest))
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.SUCCESS)
                    .data(null)
                    .msg(messageTemplate.message("error.ResultTest.createFail"))
                    .build();
            return ResponseEntity.ok(responseDTO);
        }

    }
}
