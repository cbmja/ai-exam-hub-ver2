package com.aiexamhub.exam.controller;

import com.aiexamhub.exam.dto.Member;
import com.aiexamhub.exam.dto.Page;
import com.aiexamhub.exam.dto.Repository;
import com.aiexamhub.exam.service.LoginService;
import com.aiexamhub.exam.service.RepositoryService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {

    private final LoginService loginService;
    private final RepositoryService repositoryService;


    // ok
    // 회원가입
    @PostMapping("/join")
    @ResponseBody
    public String joinProc(@RequestBody Member form){

        String res = "";

        try{
            res = loginService.join(form);
        }catch (Exception e){
            e.printStackTrace();
            res = "server err";
        }

        return res;
    }

    // ok
    // 로그인
    @PostMapping("/login")
    @ResponseBody
    public String loginProc(@RequestBody Member form, HttpServletResponse response , Model model){

        String res = "";

        try{
            res = loginService.login(form , response , model);
        }catch (Exception e){
            e.printStackTrace();
            res = "server err";
        }

        return res;
    }

    // ok
    // 로그인
    @GetMapping("/logout")
    public String logout(HttpServletResponse response , Model model){

        // 기존 로그인 정보 삭제
        Cookie oldCookie = new Cookie("mCode", "");
        oldCookie.setMaxAge(0);
        oldCookie.setPath("/");
        response.addCookie(oldCookie);

        model.addAttribute("isLogin" , false);

        return "redirect:/index";
    }
    
    // ok
    // 나의 저장소
    @GetMapping("/repository")
    public String repository(ServletRequest servletRequest, Model model , @RequestParam(name = "page" , defaultValue = "0") int page ,
                                                                         @RequestParam(name = "search" , defaultValue = "") String search ,
                                                                         @RequestParam(name = "searchType" , defaultValue = "repository_name") String searchType ,
                                                                         @RequestParam(name = "sortType" , defaultValue = "created_at") String sortType ,
                                                                         @RequestParam(name = "sort" , defaultValue = "DESC") String sort){

        try {
            HttpServletRequest req = (HttpServletRequest) servletRequest;

            String memberCode = (String)(req.getAttribute("memberCode"));

            Map<String , Object> res = repositoryService.getRepositories(page , search , searchType ,sortType ,sort);

            List<Repository> list = (List<Repository>)(res.get("list"));
            Page pageData = (Page)(res.get("page"));

            model.addAttribute("list" , list);
            model.addAttribute("isLogin" , (boolean)req.getAttribute("isLogin"));
            model.addAttribute("page" , pageData);


            return "view/member/repository";
        }catch (Exception e){
            e.printStackTrace();

            return "view/err";
        }



    }

    @GetMapping("/question")
    public String question(ServletRequest servletRequest, Model model){
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        model.addAttribute("isLogin" , (boolean)req.getAttribute("isLogin"));

        return "view/member/question";
    }

    @GetMapping("/extract")
    public String extract(ServletRequest servletRequest, Model model){
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        model.addAttribute("isLogin" , (boolean)req.getAttribute("isLogin"));

        return "view/extract/extractor";
    }





}
