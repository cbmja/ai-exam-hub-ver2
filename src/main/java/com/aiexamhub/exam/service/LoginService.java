package com.aiexamhub.exam.service;

import com.aiexamhub.exam.dto.Member;
import com.aiexamhub.exam.util.CipherUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final SqlSessionTemplate sql;
    private final CipherUtil cipherUtil;



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

        String memberCode = cipherUtil.createCode();

        form.setMemberCode(memberCode);
        form.setStatus("active");

        String encPw = cipherUtil.encrypt(form.getMemberPw());

        if(encPw.equals("err")){
            return "server err";
        }

        form.setMemberPw(encPw);

        int res = sql.insert("com.aiexamhub.exam.mapper.MemberMapper.save",form);

        if(res <= 0){
            return "sql err";
        }

        return "join success";
    }


    @Transactional
    public String login(Member form , HttpServletResponse response , Model model){

        Member member = sql.selectOne("com.aiexamhub.exam.mapper.MemberMapper.selectByMemberId" , form.getMemberId());


        if(member == null){
            return "wrong id";
        }else{
            String decPw = cipherUtil.decrypt(member.getMemberPw());

            if(!form.getMemberPw().equals(decPw)){
                return "wrong pw";
            }
        }

        String memberCode = member.getMemberCode();

        memberCode = cipherUtil.encrypt(memberCode);

        if(memberCode.equals("err")){
            return "server err";
        }

        // 기존 로그인 정보 삭제
        Cookie oldCookie = new Cookie("mCode", "");
        oldCookie.setMaxAge(0);
        oldCookie.setPath("/");
        response.addCookie(oldCookie);



        // 새 로그인 정보 쿠키 저장
        Cookie idCookie = new Cookie("mCode", memberCode);
        // 쿠키 설정
        idCookie.setHttpOnly(true);
        idCookie.setSecure(false);
        idCookie.setPath("/");
        idCookie.setMaxAge(3 * 24 * 60 * 60); // 3일 유지

        response.addCookie(idCookie);

        model.addAttribute("isLogin" , true);

        return "login success";
    }


}
