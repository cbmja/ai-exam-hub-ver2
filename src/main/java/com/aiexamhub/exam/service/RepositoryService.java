package com.aiexamhub.exam.service;

import com.aiexamhub.exam.dto.Member;
import com.aiexamhub.exam.dto.Page;
import com.aiexamhub.exam.dto.Repository;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final SqlSessionTemplate sql;


    @Transactional
    public Map<String , Object> getRepositories(int page , String search , String searchType , String sortType , String sort){

        Map<String , Object> res = new HashMap<>();

        Page form = new Page();
        form.setSearchType(searchType);
        form.setSearch(search);

        int total = sql.selectOne("com.aiexamhub.exam.mapper.RepositoryMapper.getTotal" , form);

        Page param = new Page(page , total);
        param.setSearch(search);
        param.setSearchType(searchType);
        param.setSortType(sortType);
        param.setSort(sort);

        List<Repository> list = sql.selectList("com.aiexamhub.exam.mapper.RepositoryMapper.getRepositories" , param);

        if(list == null){
            list = new ArrayList<>();
        }

        res.put("list" , list);
        res.put("page" , param);

        return res;
    }

    @Transactional
    public String edit(Repository form , ServletRequest servletRequest){


        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String memberCode = (String)(req.getAttribute("memberCode"));

        Repository repository = sql.selectOne("com.aiexamhub.exam.mapper.RepositoryMapper.selectByRepositoryCode" , form.getRepositoryCode());

        if(!repository.getMemberCode().equals(memberCode)){
            return "diff";
        }

        if(sql.update("com.aiexamhub.exam.mapper.RepositoryMapper.update" , form) <= 0){
            return "DB err";
        }

        return "success";

    }

}
