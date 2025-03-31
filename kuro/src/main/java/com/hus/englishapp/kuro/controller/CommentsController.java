package com.hus.englishapp.kuro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hus.englishapp.kuro.model.Comments;
import com.hus.englishapp.kuro.model.dto.CommentsResponse;
import com.hus.englishapp.kuro.model.dto.ResponseDTO;
import com.hus.englishapp.kuro.service.CommentsService;
import com.hus.englishapp.kuro.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@Validated
@Slf4j
public class CommentsController {
    @Autowired
    private CommentsService commentsService;

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/findAllParentCmt")
    public ResponseEntity<?> findAll() {
        List<Comments> commentsList = commentsService.findAllParentCmt();
        ObjectMapper mapper = new ObjectMapper();
        ResponseDTO responseDTO = ResponseDTO.builder()
                .code(Constants.RESPONSE_CODE.SUCCESS)
                .data(mapper.valueToTree(commentsList))
                .build();
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/findAllAnswerCmt")
    public ResponseEntity<?> findAllAnswerCmt(@RequestParam(name = "parentId") Integer parentId) {
        List<Comments> commentsList = commentsService.findAllAnswerCmtByParentId(parentId);
        ObjectMapper mapper = new ObjectMapper();
        ResponseDTO responseDTO = ResponseDTO.builder()
                .code(Constants.RESPONSE_CODE.SUCCESS)
                .data(mapper.valueToTree(commentsList))
                .build();
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createNew(@Validated @RequestBody Comments request) {
        try {
            Comments newComment = commentsService.createNewCmt(request);
            ObjectMapper mapper = new ObjectMapper();
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.SUCCESS)
                    .data(mapper.valueToTree(newComment))
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.FAILURE)
                    .data(null)
                    .build();
            return ResponseEntity.ok(responseDTO);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/deleteById")
    public ResponseEntity<?> deleteById(@Validated @RequestBody CommentsResponse commentsResponse) {
        try {
            commentsService.deleteById(commentsResponse);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.SUCCESS)
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.FAILURE)
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        }
    }
}
