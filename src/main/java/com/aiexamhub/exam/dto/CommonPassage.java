package com.aiexamhub.exam.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CommonPassage {

    private Integer commonPassageCode;
    private String content;
    private String examCateCode;
    private Integer examYear;
    private String examType;
    private Integer examMonth;
    private String subjectCode;
    private String subjectDetailCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
