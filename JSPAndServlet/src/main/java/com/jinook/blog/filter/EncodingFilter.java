package com.jinook.blog.filter;

import jakarta.servlet.*;
import java.io.IOException;

/**
 * 모든 요청/응답의 문자 인코딩을 UTF-8로 설정하는 필터.
 * Spring Boot에서는 기본적으로 CharacterEncodingFilter가 자동 적용된다.
 */
public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }
}
