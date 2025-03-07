package com.hus.englishapp.kuro.repository;

import com.hus.englishapp.kuro.model.dto.ResultTestResponseDto;

import java.util.List;

public interface ResultTestRepositoryCustom {
    ResultTestResponseDto findAllToGetQues(String sectionId, String UserId);
}
