package com.hus.englishapp.kuro.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentsRequest {
    private Integer id;
    private Integer parentId;
    private String sectionId;
    private String userId;
    private String userName;
    private Integer likesCount;
    private Integer repliesCount;
}
