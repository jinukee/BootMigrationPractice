package com.jinook.bootmigration.config;

import com.jinook.bootmigration.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 MVC 설정.
 *
 * [JSP 대응]
 * JSP에서는 web.xml에 Filter를 등록하거나, 각 Command에서 직접 세션을 체크했다.
 * Spring Boot에서는 WebMvcConfigurer를 구현하여 Interceptor를 등록하고,
 * URL 패턴 기반으로 적용 범위를 세밀하게 조절할 수 있다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/**")                   // 모든 경로에 적용
                .excludePathPatterns(                      // 로그인 없이 접근 가능한 경로
                        "/",                               // 홈 (게시글 목록)
                        "/posts",                          // 게시글 목록
                        "/post/{id}",                      // 게시글 상세 (조회만)
                        "/user/login",                     // 로그인 페이지
                        "/user/register",                  // 회원가입 페이지
                        "/css/**",                         // 정적 리소스
                        "/error"                           // 에러 페이지
                );
    }
}
