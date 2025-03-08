package com.aiexamhub.exam.mapper;

import com.aiexamhub.exam.dto.Subject;

import java.util.List;

public interface SubjectMapper {


    List<Subject>selectByCateCode(String examCateCode);
}
