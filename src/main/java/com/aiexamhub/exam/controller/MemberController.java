package com.aiexamhub.exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class MemberController {

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


}
