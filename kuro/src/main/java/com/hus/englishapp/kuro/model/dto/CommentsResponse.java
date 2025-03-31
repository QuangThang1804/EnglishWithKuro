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
public class CommentsResponse {
    private Integer id;
    private Integer parentId;
    private String sectionId;
    private String userId;
    private String userName;
    private Date createdDate;
    private Date updatedDate;
    private Integer likesCount;
    private Integer repliesCount;
}
