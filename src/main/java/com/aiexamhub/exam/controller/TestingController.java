package com.aiexamhub.exam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestingController {


    @GetMapping("/questions")
    public String questions(){
        return "view/mypage/questions";
    }


    @GetMapping("/repositories")
    public String repositories(){
        return "view/mypage/repositories";
    }
}
