package com.hus.englishapp.kuro.repository;

import com.hus.englishapp.kuro.model.Section;
import com.hus.englishapp.kuro.model.SectionContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionContentRepository extends JpaRepository<SectionContent, String>, JpaSpecificationExecutor<SectionContent> {
    @Query(value = "select * from SECTION_CONTENT where section_id = :sectionId and section_kind = :sectionKind and section_name = :sectionName ", nativeQuery = true)
    List<SectionContent> findListSectionQues(@Param("sectionId") String sectionId, @Param("sectionKind") String sectionKind, @Param("sectionName") String sectionName);

    Optional<SectionContent> findById(String id);
}
