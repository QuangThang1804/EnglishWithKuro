package com.hus.englishapp.kuro.repository.impl;

import com.hus.englishapp.kuro.model.Section;
import com.hus.englishapp.kuro.model.dto.SectionRequestDto;
import com.hus.englishapp.kuro.model.dto.SectionResponseContentDto;
import com.hus.englishapp.kuro.model.dto.SectionResponseDetailDto;
import com.hus.englishapp.kuro.repository.SectionRepository;
import com.hus.englishapp.kuro.repository.SectionRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.apache.commons.lang3.StringUtils;
import com.hus.englishapp.kuro.util.DataConvertUtil;


import java.util.*;

public class SectionRepositoryImpl implements SectionRepositoryCustom {
    @PersistenceContext(unitName = "entityManagerFactory")
    EntityManager entityManager;

    @Override
    public Page<SectionResponseDetailDto> search(Pageable pageable, String sectionKind, String sectionName) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from defaultdb.SECTION_SYS t1 ");
        sql.append(" WHERE 1=1 ");

        if (StringUtils.isNotBlank(sectionKind)) {
            sql.append(" AND t1.SECTION_KIND = :sectionKind");
        }

        if (StringUtils.isNotBlank(sectionName)) {
            sql.append(" AND t1.SECTION_NAME = :sectionName");
        }

        Query query = entityManager.createNativeQuery(sql.toString());
        if (StringUtils.isNotBlank(sectionKind)) {
            query.setParameter("sectionKind", sectionKind);
        }

        if (StringUtils.isNotBlank(sectionName)) {
            query.setParameter("sectionName", sectionName);
        }
        List<Object[]> count = query.getResultList();
        List<SectionResponseDetailDto> listSection = new ArrayList<>();
        for (Object[] section: count) {
            SectionResponseDetailDto newSection = new SectionResponseDetailDto();
            newSection.setId(DataConvertUtil.safeToString(section[0]));
            newSection.setSectionKind(DataConvertUtil.safeToString(section[1]));
            newSection.setSectionName(DataConvertUtil.safeToString(section[2]));
            listSection.add(newSection);
        }
        return new PageImpl<>(listSection);
    }
}
