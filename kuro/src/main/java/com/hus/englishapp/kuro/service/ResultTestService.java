package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.model.Ques;
import com.hus.englishapp.kuro.model.ResultTest;
import com.hus.englishapp.kuro.model.dto.ResultTestResponseDto;
import com.hus.englishapp.kuro.repository.ResultTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResultTestService {
    @Autowired
    private ResultTestRepository resultTestRepository;

    @Transactional
    public List<ResultTest> create(String sectionId, String userId, List<String> quesIds) {
        resultTestRepository.deleteBySectionIdAndUserId(sectionId, userId);
        List<ResultTest> listResultWrongQues = new ArrayList<>();
        for (String quesId: quesIds) {
            ResultTest resultTest = new ResultTest();
            resultTest.setSectionId(sectionId);
            resultTest.setUserId(userId);
            resultTest.setWrongQuesId(quesId);
            resultTestRepository.save(resultTest);
            listResultWrongQues.add(resultTest);
        }
        return listResultWrongQues;
    }

    public ResultTestResponseDto findAllBySectionIdAndUserId(String sectionId, String userId) {
        return resultTestRepository.findAllToGetQues(sectionId, userId);
    }
}
