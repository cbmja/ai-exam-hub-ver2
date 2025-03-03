package com.aiexamhub.exam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestingController {


    @GetMapping("/question")
    public String questions(){
        return "view/mypage/question";
    }


    @GetMapping("/repository")
    public String repositories(){
        return "view/mypage/repository";
    }

    @GetMapping("/extract")
    public String extract(){
        return "view/extract/extractor";
    }
}
