package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.model.Comment;
import com.hus.englishapp.kuro.model.dto.CommentResponse;
import com.hus.englishapp.kuro.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageImpl;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public Page<Comment> findAll(Pageable pageable) {
        List<Comment> commentList = commentRepository.findAll();
        return new PageImpl<>(commentList);
    }

    public Comment update(Comment comment) {
        Comment cmt = new Comment();
        if (comment.getId() != null) {
            Optional<Comment> cmtInDb = commentRepository.findById(comment.getId());
            cmtInDb.ifPresent(value -> cmt.setId(value.getId()));
        }
        cmt.setSectionId(comment.getSectionId());
        cmt.setUserId(comment.getUserId());
        cmt.setComment(comment.getComment());
        commentRepository.save(cmt);
        return cmt;
    }

    public void deleteById(Integer id) {
        commentRepository.deleteById(id);
    }
}
