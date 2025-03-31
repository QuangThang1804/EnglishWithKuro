package com.hus.englishapp.kuro.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hus.englishapp.kuro.util.DateDeserializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "COMMENTS")
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "PARENT_ID")
    private Integer parentId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "SECTION_ID")
    private String sectionId;

    @Column(name = "CONTENT")
    private String content;

    @JsonDeserialize(using = DateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    @CreatedDate
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @JsonDeserialize(using = DateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    @LastModifiedDate
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;

    @Column(name = "LIKES_COUNT")
    private Integer likesCount;

    @Column(name = "REPLIES_COUNT")
    private Integer repliesCount;
}
