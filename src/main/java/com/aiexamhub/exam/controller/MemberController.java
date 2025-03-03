package com.aiexamhub.exam.controller;

import com.aiexamhub.exam.dto.Member;
import com.aiexamhub.exam.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
public class MemberController {

    private final LoginService loginService;

    @GetMapping("/repository")
    public String repository(){
        return "view/member/repository";
    }

    @GetMapping("/question")
    public String question(){
        return "view/member/question";
    }

    @GetMapping("/extract")
    public String extract(){
        return "view/extract/extractor";
    }

    // ok
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



}
