package com.aiexamhub.exam.filter;

import com.aiexamhub.exam.util.CipherUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

@RequiredArgsConstructor
@Component
public class LoginFilter implements Filter {

    private final CipherUtil cipherUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        String reqUri = req.getRequestURI();
        System.out.println(reqUri+"==============================================");

        if (reqUri.endsWith(".js") || reqUri.endsWith(".css")) {
            System.out.println("1-static");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Cookie[] cookies = req.getCookies();
        String idCookie = "";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("mCode".equals(cookie.getName())) {
                    idCookie = cookie.getValue();
                    break;
                }
            }
        }

        Boolean isLogin = !idCookie.isEmpty();
        servletRequest.setAttribute("isLogin", isLogin);
        System.out.println("isLogin : "+isLogin);

        if (isLogin) {
            idCookie = cipherUtil.decrypt(idCookie);
        }
        servletRequest.setAttribute("memberCode", idCookie);

        // 로그인 없이 접근 가능한 API 목록
        Set<String> logOutPath = new HashSet<>();
        logOutPath.add("/index");
        logOutPath.add("/member/join");
        logOutPath.add("/member/login");
        logOutPath.add("/member/logout");

        // 로그아웃 상태에서 접근 가능한 경로 확인
        if (logOutPath.contains(reqUri)) {
            System.out.println("2-can logout");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 로그인 상태가 아니라면 `/index`로 리다이렉트
        if (!isLogin) {
            System.out.println("3-need login -> redirect /index");
            httpResponse.sendRedirect("/index");
            return;
        }
        System.out.println("4-can logout");
        filterChain.doFilter(servletRequest, servletResponse);
    }





}
