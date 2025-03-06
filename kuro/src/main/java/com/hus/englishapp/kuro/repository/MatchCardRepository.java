package com.hus.englishapp.kuro.repository;

import com.hus.englishapp.kuro.model.MatchCard;
import com.hus.englishapp.kuro.model.dto.MatchCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchCardRepository extends JpaRepository<MatchCard, Integer>, JpaSpecificationExecutor<MatchCard> {
    @Query(value = "SELECT t1.id, t1.SECTION_ID, u.username AS userName, t1.TIME_TEST FROM MATCH_CARD t1 LEFT JOIN User u ON t1.USER_ID = u.id " +
            "WHERE t1.SECTION_ID = '166835cc-b625-4aef-8eee-f3a7a35774fa' ORDER BY t1.TIME_TEST", nativeQuery = true)
    List<MatchCardResponse> findAllBySection(@Param(value = "sectionId") String sectionId);
}
