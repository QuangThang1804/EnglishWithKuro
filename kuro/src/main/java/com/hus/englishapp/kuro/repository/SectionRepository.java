package com.hus.englishapp.kuro.repository;

import com.hus.englishapp.kuro.model.Section;
import com.hus.englishapp.kuro.model.SectionContent;
import com.hus.englishapp.kuro.model.dto.SectionRequestDto;
import com.hus.englishapp.kuro.model.dto.SectionResponseDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, String>, JpaSpecificationExecutor<Section>, SectionRepositoryCustom {
    @Query(value = "SELECT * FROM defaultdb.SECTION_SYS;", nativeQuery = true)
    Page<Section> findAll(Pageable pageable);

    @Query("SELECT new com.hus.englishapp.kuro.model.dto.SectionResponseDetailDto(sc.id, sc.sectionKind, sc.sectionName) " +
            "FROM Section sc " +
            "WHERE (:kind IS NULL OR sc.sectionKind = :kind) " +
            "AND (:name IS NULL OR sc.sectionName = :name)")
    Page<SectionResponseDetailDto> searchAll(String name, String kind, Pageable pageable);
    Optional<Section> findById(String id);
}
