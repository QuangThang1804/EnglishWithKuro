package com.hus.englishapp.kuro.repository;

import com.hus.englishapp.kuro.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Integer>, JpaSpecificationExecutor<Comments> {
    @Query(value = "select * from COMMENTS where PARENT_ID IS NULL order by CREATED_DATE desc", nativeQuery = true)
    List<Comments> findAllParentCmt();

    @Query(value = "select * from COMMENTS t1 where PARENT_ID = :parentId order by CREATED_DATE", nativeQuery = true)
    List<Comments> findAllAnswerCmt(@Param(value = "parentId") Integer parentId);

    List<Comments> findAllByParentId(Integer parentId);
}
