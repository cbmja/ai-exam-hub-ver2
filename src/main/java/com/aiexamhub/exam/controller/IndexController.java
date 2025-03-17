package com.aiexamhub.exam.controller;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class IndexController {

    // ok
    // 메인
    @GetMapping("/index")
    public String repository(ServletRequest servletRequest, Model model){
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        model.addAttribute("isLogin" , (boolean)req.getAttribute("isLogin"));
        return "view/index";
    }

    // ok
    // 메인
    @GetMapping("/test")
    public String test(ServletRequest servletRequest, Model model){
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        model.addAttribute("isLogin" , (boolean)req.getAttribute("isLogin"));
        return "view/extract/test";
    }

}
