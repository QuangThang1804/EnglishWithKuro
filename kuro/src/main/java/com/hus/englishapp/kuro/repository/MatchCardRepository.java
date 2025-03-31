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
import java.util.Optional;

@Repository
public interface MatchCardRepository extends JpaRepository<MatchCard, Integer>, JpaSpecificationExecutor<MatchCard> {
    @Query(value = "SELECT t1.id, t1.SECTION_ID, u.fullname AS userName, t1.TIME_TEST " +
            "FROM MATCH_CARD t1 " +
            "JOIN User u ON t1.USER_ID = u.id " +
            "WHERE t1.SECTION_ID = :sectionId ORDER BY t1.TIME_TEST", nativeQuery = true)
    List<MatchCardResponse> findAllBySection(@Param(value = "sectionId") String sectionId);

    @Query(value = "from MATCH_CARD mc " +
            "where mc.sectionId = :sectionId and mc.userId = :userId")
    Optional<MatchCard> findBySectionIdAndUserId(String userId, String sectionId);
}
