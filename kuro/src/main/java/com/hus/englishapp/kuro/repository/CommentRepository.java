package com.hus.englishapp.kuro.repository;

import com.hus.englishapp.kuro.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer>, JpaSpecificationExecutor<Comment> {
    Optional<Comment> findByIdAndSectionId(Integer id, String sectionId);
}
