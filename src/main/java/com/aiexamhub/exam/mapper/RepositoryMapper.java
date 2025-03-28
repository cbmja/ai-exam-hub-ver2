package com.aiexamhub.exam.mapper;

import com.aiexamhub.exam.dto.Page;
import com.aiexamhub.exam.dto.Repository;

import java.util.List;

public interface RepositoryMapper {

    List<Repository> getRepositories(Page page);

    int getTotal(Page page);

    Repository selectByRepositoryCode(String repositoryCode);

    int save(Repository form);

    int update(Repository form);

}
