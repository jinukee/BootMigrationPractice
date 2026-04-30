package com.jinook.bootmigration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정.
 * [Before: WebConfig + LoginCheckInterceptor]
 * - HandlerInterceptor로 URL 패턴별 세션 체크
 * - excludePathPatterns()로 허용 URL을 수동 나열
 * - 비밀번호는 SHA-256으로 수동 해싱
 * [After: SecurityConfig]
 * - SecurityFilterChain으로 인증/인가를 선언적으로 설정
 * - .permitAll() / .authenticated()로 접근 제어
 * - BCryptPasswordEncoder로 비밀번호 자동 해싱/검증
 * - 로그인/로그아웃 처리를 Spring Security가 자동으로 수행
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * SecurityFilterChain: HTTP 요청에 대한 보안 정책을 정의한다.
     * [흐름]
     * 모든 HTTP 요청 → SecurityFilterChain → 인증 확인 → 통과/차단
     * [LoginCheckInterceptor와의 차이]
     * - Interceptor: DispatcherServlet 이후에 동작 (Controller 직전)
     * - SecurityFilter: DispatcherServlet 이전에 동작 (더 앞단에서 차단)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. URL별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/user/login",      // 로그인 페이지
                    "/user/register",   // 회원가입 페이지
                    "/css/**",          // 정적 리소스
                    "/error"            // 에러 페이지
                ).permitAll()           // 위 경로는 비로그인 접근 허용
                .anyRequest().authenticated()  // 나머지는 모두 로그인 필요
            )

            // 2. 폼 로그인 설정
            .formLogin(form -> form
                .loginPage("/user/login")        // 우리가 만든 로그인 페이지 사용
                .loginProcessingUrl("/user/login") // 로그인 폼의 action URL
                .usernameParameter("email")      // 기본값 "username" → "email"로 변경
                .defaultSuccessUrl("/", true)     // 로그인 성공 시 홈으로
                .failureUrl("/user/login?error=true") // 실패 시 에러 파라미터 추가
                .permitAll()
            )

            // 3. 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/user/logout")       // 로그아웃 URL
                .logoutSuccessUrl("/user/login")  // 로그아웃 성공 시 로그인 페이지로
                .invalidateHttpSession(true)     // 세션 무효화
                .permitAll()
            );

        return http.build();
    }

    /**
     * 비밀번호 인코더.
     * [Before: SHA-256]
     * - Salt 없음 → 같은 비밀번호는 항상 같은 해시값
     * - Rainbow Table 공격에 취약
     * [After: BCrypt]
     * - 자동으로 랜덤 Salt를 생성하여 해시에 포함
     * - 같은 비밀번호라도 매번 다른 해시값 생성
     * - 비밀번호 검증 시 해시에서 Salt를 추출하여 비교
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
