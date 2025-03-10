package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.model.Section;
import com.hus.englishapp.kuro.model.SectionContent;
import com.hus.englishapp.kuro.repository.SectionContentRepository;
import com.hus.englishapp.kuro.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;
import java.util.*;

@Service
public class ExcelService {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SectionContentRepository sectionContentRepository;

    public List<SectionContent> readExcelFile(String sectionKind, String sectionName, MultipartFile file) {
        List<SectionContent> listSections = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            boolean firstRow = true;

            Section newSection = Section.builder()
                    .id(UUID.randomUUID().toString())
                    .sectionKind(sectionKind)
                    .sectionName(sectionName)
                    .build();
            sectionRepository.save(newSection);
            while (rows.hasNext()) {
                Row row = rows.next();

                // Skip first row if it's title
                if (firstRow) {
                    firstRow = false;
                    continue;
                }

                if (row.getCell(0) != null) {
                    if (!Objects.equals(row.getCell(0).getStringCellValue(), "")) {
                        String question = row.getCell(0).getStringCellValue();
                        String answer = row.getCell(1).getStringCellValue();
                        SectionContent newSectionContent = SectionContent.builder()
                                .id(UUID.randomUUID().toString())
                                .sectionId(newSection.getId())
                                .sectionKind(newSection.getSectionKind())
                                .sectionName(newSection.getSectionName())
                                .question(question)
                                .answer(answer)
                                .build();

//                                new SectionContent(UUID.randomUUID().toString(), newSection.getId(), newSection.getSectionKind(), newSection.getSectionName(), sectionQues);
                        listSections.add(newSectionContent);
                        sectionContentRepository.save(newSectionContent);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listSections;
    }
}
