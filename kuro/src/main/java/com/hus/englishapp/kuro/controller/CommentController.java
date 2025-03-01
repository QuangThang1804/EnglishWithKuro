package com.hus.englishapp.kuro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hus.englishapp.kuro.config.MessageTemplate;
import com.hus.englishapp.kuro.model.Comment;
import com.hus.englishapp.kuro.model.dto.ResponseDTO;
import com.hus.englishapp.kuro.service.CommentService;
import com.hus.englishapp.kuro.util.Constants;
import com.hus.englishapp.kuro.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@Validated
@Slf4j
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private MessageTemplate messageTemplate;

    @PreAuthorize("ROLE_USER")
    @GetMapping("/findAll")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") Integer size,
            @RequestParam(name = "sort", required = false) List<String> sorts) throws Exception {
        Page<Comment> commentPage = commentService.findAll(PagingUtil.buildPageable(page, size, sorts));
        ObjectMapper mapper = new ObjectMapper();
        ResponseDTO responseDTO = ResponseDTO.builder()
                .code(Constants.RESPONSE_CODE.SUCCESS)
                .data(mapper.valueToTree(commentPage.getContent()))
                .build();
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("ROLE_USER")
    @PostMapping("/update")
    public ResponseEntity<?> create(@Validated @RequestBody Comment comment) {
        try {
            Comment cmt = commentService.update(comment);
            ObjectMapper mapper = new ObjectMapper();
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.SUCCESS)
                    .data(mapper.valueToTree(cmt))
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

    @PreAuthorize("ROLE_USER")
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable @NotNull Integer id) {
        try {
            commentService.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.SUCCESS)
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        }  catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .code(Constants.RESPONSE_CODE.FAILURE)
                    .data(null)
                    .msg(messageTemplate.message("error.delete.fail"))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        }
    }
}
