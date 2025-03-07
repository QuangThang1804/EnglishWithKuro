package com.hus.englishapp.kuro.repository;

import com.hus.englishapp.kuro.model.ResultTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ResultTestRepository extends JpaRepository<ResultTest, Integer>, JpaSpecificationExecutor<ResultTest>, ResultTestRepositoryCustom {
//    void deleteAllByWrongQuesIdIn(List<String> wrongQuesId);

    void deleteBySectionIdAndUserId(String sectionId, String userId);

    List<ResultTest> findAllBySectionIdAndUserId(String sectionId, String userId);
}
