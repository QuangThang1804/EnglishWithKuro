package com.hus.englishapp.kuro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hus.englishapp.kuro.config.MessageTemplate;
import com.hus.englishapp.kuro.model.Section;
import com.hus.englishapp.kuro.model.dto.ResponseDTO;
import com.hus.englishapp.kuro.model.dto.SectionRequestDto;
import com.hus.englishapp.kuro.model.dto.SectionResponseDetailDto;
import com.hus.englishapp.kuro.service.SectionService;
import com.hus.englishapp.kuro.util.Constants;
import com.hus.englishapp.kuro.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/section")
@Validated
@Slf4j
public class SectionController {
    @Autowired
    private MessageTemplate messageTemplate;

    @Autowired
    private SectionService sectionService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "1000") Integer size,
        @RequestParam(name = "sort", required = false) List<String> sorts) throws Exception {
        Page<SectionResponseDetailDto> sectionList = sectionService.findAll(PagingUtil.buildPageable(page, size, sorts));
        ObjectMapper mapper = new ObjectMapper();
        ResponseDTO responseDTO = ResponseDTO.builder()
                .code(Constants.RESPONSE_CODE.SUCCESS)
                .data(mapper.valueToTree(sectionList.getContent()))
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") Integer size,
            @RequestParam(name = "sort", required = false) List<String> sorts,
            @RequestParam(required = false) String sectionKind,
            @RequestParam(required = false) String sectionName) throws Exception {
        Page<SectionResponseDetailDto> sectionList = sectionService.search(PagingUtil.buildPageable(page, size, sorts), sectionKind, sectionName);
        ObjectMapper mapper = new ObjectMapper();
        ResponseDTO responseDTO = ResponseDTO.builder()
                .code(Constants.RESPONSE_CODE.SUCCESS)
                .data(mapper.valueToTree(sectionList.getContent()))
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Validated @RequestBody SectionRequestDto sectionRequestDto) {
        try {
            Section section = sectionService.update(sectionRequestDto);
            ObjectMapper mapper = new ObjectMapper();
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.SUCCESS)
                    .data(mapper.valueToTree(section))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.FAILURE)
                    .data(null)
                    .msg(messageTemplate.message("error.SectorController.createFail"))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@Validated @RequestBody SectionRequestDto sectionRequestDto) {
        try {
            Section section = sectionService.update(sectionRequestDto);
            ObjectMapper mapper = new ObjectMapper();
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.SUCCESS)
                    .data(mapper.valueToTree(section))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.FAILURE)
                    .data(null)
                    .msg(messageTemplate.message("error.SectorController.updateFail"))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable @NotNull String id) {
        try {
            sectionService.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.SUCCESS)
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        }  catch (Exception e) {
            log.error(e.getMessage());
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.FAILURE)
                    .data(null)
                    .msg(messageTemplate.message("error.SectorController.deleteFail"))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        }
    }
}
