package com.hus.englishapp.kuro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hus.englishapp.kuro.config.MessageTemplate;
import com.hus.englishapp.kuro.model.SectionContent;
import com.hus.englishapp.kuro.model.dto.ResponseDTO;
import com.hus.englishapp.kuro.service.SectionQuesService;
import com.hus.englishapp.kuro.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sectionQues")
@Validated
@Slf4j
public class SectionQuesController {
    @Autowired
    private MessageTemplate messageTemplate;

    @Autowired
    private SectionQuesService sectionQuesService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        SectionContent sectionContent = sectionQuesService.findById(id);
        ObjectMapper mapper = new ObjectMapper();
        ResponseDTO responseDTO = ResponseDTO.builder()
                .code(Constants.RESPONSE_CODE.SUCCESS)
                .data(mapper.valueToTree(sectionContent))
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }
}
