package com.aiexamhub.exam.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Member {

    private String memberCode;
    private String email;
    private String memberId;
    private String memberPw;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;

    private String memberPw2;

}
