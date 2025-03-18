package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.model.Comments;
import com.hus.englishapp.kuro.model.dto.CommentsResponse;
import com.hus.englishapp.kuro.repository.CommentsRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommentsService {
    @Autowired
    private CommentsRepository commentsRepository;

    @Transactional
    public Comments createNewCmt(Comments request) {
//        Comments comments = Comments.builder()
//                .parentId(null)
//                .userId(request.getUserId())
//                .sectionId(request.getSectionId())
//                .content(request.getContent())
//                .createdDate(new Date())
//                .updatedDate(new Date())
//                .likesCount(0)
//                .repliesCount(0)
//                .build();
        Comments comments = new Comments();
        if (request.getId() != null) {
            Optional<Comments> commentsInDb = commentsRepository.findById(request.getId());
            if (commentsInDb.isPresent()) {
                comments = commentsInDb.get();
            }
        } else {
            if (request.getParentId() != null) {
                Optional<Comments> parentCommentsInDb = commentsRepository.findById(request.getParentId());
                if (parentCommentsInDb.isPresent()) {
                    Comments parentCmt = parentCommentsInDb.get();
                    parentCmt.setRepliesCount(parentCmt.getRepliesCount() + 1);
                    commentsRepository.save(parentCmt);
                }
                comments.setParentId(request.getParentId());
            }
            comments.setCreatedDate(new Date());
            comments.setUserId(request.getUserId());
            comments.setSectionId(request.getSectionId());
        }

//        if (request.getParentId() != null) {
//            comments.setParentId(request.getParentId());
//            Optional<Comments> parentComments = commentsRepository.findById(request.getParentId());
//            if (parentComments.isPresent()) {
//                parentComments.get().setRepliesCount(parentComments.get().getLikesCount() + 1);
//                commentsRepository.save(parentComments.get());
//            }
//        }
        comments.setContent(request.getContent());
        comments.setUpdatedDate(new Date());
        comments.setLikesCount(request.getLikesCount());
        comments.setRepliesCount(request.getRepliesCount());
        return commentsRepository.save(comments);
    }

    public List<Comments> findAllParentCmt() {
        return commentsRepository.findAllParentCmt();
    }

    public List<Comments> findAllAnswerCmtByParentId(Integer parentId) {
        return commentsRepository.findAllAnswerCmt(parentId);
    }

    @Transactional
    public void deleteById(CommentsResponse commentsResponse) {
        if (commentsResponse.getParentId() == null) {
            List<Comments> commentsList = commentsRepository.findAllByParentId(commentsResponse.getId());
//            for (Comments comments: commentsList) {
//                commentsRepository.deleteById(comments.getId());
//            }
            List<Integer> listParentIds = new ArrayList<>();
            for (Comments comments: commentsList) {
                listParentIds.add(comments.getId());
            }
            commentsRepository.deleteAllById(listParentIds);
        } else {
            Optional<Comments> commentsInDb = commentsRepository.findById(commentsResponse.getParentId());
            if (commentsInDb.isPresent()) {
                commentsInDb.get().setRepliesCount(commentsResponse.getRepliesCount() - 1);
                commentsRepository.save(commentsInDb.get());
            }
        }
        commentsRepository.deleteById(commentsResponse.getId());
    }
}
