package com.aiexamhub.exam.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Repository {
    private String repositoryCode;
    private String repositoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String memberCode;
    private String description;

    private int questionCnt;

}
