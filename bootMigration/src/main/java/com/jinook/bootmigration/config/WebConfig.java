package com.jinook.bootmigration.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 MVC 설정.
 * [변경 이력]
 * Before: LoginCheckInterceptor를 등록하여 세션 기반 인증을 수행했다.
 * After:  Spring Security의 SecurityFilterChain이 인증/인가를 대체하므로
 * 인터셉터 등록 코드를 제거했다. 향후 다른 MVC 설정이 필요할 때 이 클래스를 활용하도록 한기 위해 껍데기는 남겨놓는걸로.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
}
