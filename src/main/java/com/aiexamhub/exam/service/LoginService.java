package com.aiexamhub.exam.service;

import com.aiexamhub.exam.dto.Member;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final SqlSessionTemplate sql;



    @Transactional
    public String join(Member form){

            int IdCheck = sql.selectOne("com.aiexamhub.exam.mapper.MemberMapper.countByMemberId",form.getMemberId());
            int emailCheck = sql.selectOne("com.aiexamhub.exam.mapper.MemberMapper.countByEmail",form.getEmail());
            int phoneCheck = sql.selectOne("com.aiexamhub.exam.mapper.MemberMapper.countByPhone",form.getPhone());

            if(emailCheck > 0){
                return "duplicate email";
            }

            if(IdCheck > 0){
                return "duplicate id";
            }

            if(phoneCheck > 0){
                return "duplicate phone";
            }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH:mm:ss.SSSSSSSSS");
        Random random = new Random();
        char r1 = (char) (random.nextBoolean() ? random.nextInt(26) + 'A' : random.nextInt(26) + 'a');
        char r2 = (char) (random.nextBoolean() ? random.nextInt(26) + 'A' : random.nextInt(26) + 'a');
        char r3 = (char) (random.nextBoolean() ? random.nextInt(26) + 'A' : random.nextInt(26) + 'a');

        int i1 = random.nextInt(10);
        int i2 = random.nextInt(10);
        int i3 = random.nextInt(10);

        String memberCode = String.valueOf(r1) + i1 + String.valueOf(r2) + i2 + String.valueOf(r3) + i3 +"_" + now.format(formatter);

        form.setMemberCode(memberCode);
        form.setStatus("active");

        int res = sql.insert("com.aiexamhub.exam.mapper.MemberMapper.save",form);

        if(res <= 0){
            return "sql err";
        }

        return "join success";
    }


}
