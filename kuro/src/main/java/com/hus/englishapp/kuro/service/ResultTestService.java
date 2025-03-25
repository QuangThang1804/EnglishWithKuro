package com.hus.englishapp.kuro.service;

import com.hus.englishapp.kuro.exception.AppException;
import com.hus.englishapp.kuro.model.Ques;
import com.hus.englishapp.kuro.model.ResultTest;
import com.hus.englishapp.kuro.model.dto.ResultTestResponseDto;
import com.hus.englishapp.kuro.repository.ResultTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResultTestService {
    @Autowired
    private ResultTestRepository resultTestRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public List<ResultTest> create(String sectionId, List<String> quesIds) {
        try {
            String userId = userService.currUser().getId();
            if (userId == null) {
                throw new AppException("Không tìm thấy tài khoản người dùng hiện tại", HttpStatus.UNAUTHORIZED);
            }

            resultTestRepository.deleteBySectionIdAndUserId(sectionId, userId);
            List<ResultTest> listResultWrongQues = new ArrayList<>();

            for (String quesId : quesIds) {
                ResultTest resultTest = new ResultTest();
                resultTest.setSectionId(sectionId);
                resultTest.setUserId(userId);
                resultTest.setWrongQuesId(quesId);
                resultTestRepository.save(resultTest);
                listResultWrongQues.add(resultTest);
            }
            return listResultWrongQues;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ResultTestResponseDto findAllBySectionIdAndUserId(String sectionId) {
        String userId = userService.currUser().getId();
        return resultTestRepository.findAllToGetQues(sectionId, userId);
    }
}
