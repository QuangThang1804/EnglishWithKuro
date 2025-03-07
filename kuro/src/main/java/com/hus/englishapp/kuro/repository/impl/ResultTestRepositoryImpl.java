package com.hus.englishapp.kuro.repository.impl;

import com.hus.englishapp.kuro.model.Ques;
import com.hus.englishapp.kuro.model.dto.ResultTestResponseDto;
import com.hus.englishapp.kuro.repository.ResultTestRepository;
import com.hus.englishapp.kuro.repository.ResultTestRepositoryCustom;
import com.hus.englishapp.kuro.util.DataConvertUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultTestRepositoryImpl implements ResultTestRepositoryCustom {
    @PersistenceContext(unitName = "entityManagerFactory")
    EntityManager entityManager;

    @Override
    public ResultTestResponseDto findAllToGetQues(String sectionId, String userId) {
        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        sql.append(" SELECT t1.id, t1.SECTION_ID, t1.USER_ID, t1.WRONG_QUES_ID, u.SECTION_QUES ");
        sql.append(" FROM RESULT_TEST t1 ");
        sql.append(" LEFT JOIN SECTION_CONTENT u ON t1.WRONG_QUES_ID = u.id ");
        sql.append(" where 1 = 1 ");

        if (StringUtils.isNotBlank(sectionId)) {
            sql.append(" AND t1.SECTION_ID = :sectionId ");
        }

        if (StringUtils.isNotBlank(userId)) {
            sql.append(" AND t1.USER_ID = :userId ");
        }

        Query query = entityManager.createNativeQuery(sql.toString());
        if (StringUtils.isNotBlank(sectionId)) {
            query.setParameter("sectionId", sectionId);
        }

        if (StringUtils.isNotBlank(userId)) {
            query.setParameter("userId", userId);
        }

        List<Object[]> count = query.getResultList();
        ResultTestResponseDto resultTestResponseDto = new ResultTestResponseDto();
        resultTestResponseDto.setId(DataConvertUtil.safeToInt(count.get(0)[0]));
        resultTestResponseDto.setSectionId(DataConvertUtil.safeToString(count.get(0)[1]));
        resultTestResponseDto.setUserId(DataConvertUtil.safeToString(count.get(0)[2]));
        List<Ques> listQues = new ArrayList<>();
        for (Object[] obj: count) {
            String[] splitQues = DataConvertUtil.safeToString(obj[4]).split(":");
            Ques ques = new Ques();
            ques.setQuesId(DataConvertUtil.safeToString(obj[3]));
            ques.setS1LanguageWords(splitQues[0]);
            ques.setS2LanguageWords(splitQues[1]);
            listQues.add(ques);
        }
        resultTestResponseDto.setListQues(listQues);
        return resultTestResponseDto;
    }
}
