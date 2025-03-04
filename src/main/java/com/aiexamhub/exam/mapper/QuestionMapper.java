package com.aiexamhub.exam.mapper;

import com.aiexamhub.exam.dto.Page;
import com.aiexamhub.exam.dto.Question;

import java.util.List;

public interface QuestionMapper {


    List<Question> selectByRepositoryCode(Page page);
}
