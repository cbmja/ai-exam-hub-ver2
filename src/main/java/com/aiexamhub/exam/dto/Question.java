package com.aiexamhub.exam.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Question {
    private String questionCode;
    private String examOrgCode;
    private String examCateCode;
    private Integer examYear;
    private Integer examMonth;
    private String subjectCode;
    private String subjectDetailCode;
    private String examType;
    private String repositoryCode;
    private Integer questionNo;
    private String questionMain;
    private String questionSub;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String option5;
    private String description;
    private Integer commonPassageCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
