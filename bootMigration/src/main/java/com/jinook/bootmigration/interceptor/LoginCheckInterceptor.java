package com.jinook.bootmigration.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 로그인 체크 인터셉터.
 *
 * [JSP 대응]
 * JSP에서는 각 Command의 execute() 메서드 시작 부분에서 세션을 확인했다:
 *   HttpSession session = request.getSession(false);
 *   if (session == null || session.getAttribute("loginUser") == null) {
 *       response.sendRedirect("/user/login.do");
 *       return null;
 *   }
 * → 로그인이 필요한 모든 Command에 위 코드가 중복되었다.
 *
 * Spring Boot에서는 HandlerInterceptor를 사용하여
 * 로그인 체크 로직을 한 곳에 집중시키고, WebConfig에서 URL 패턴으로 적용 범위를 지정한다.
 */
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect("/user/login");
            return false;  // 컨트롤러 실행하지 않음
        }

        return true;  // 컨트롤러 실행 진행
    }
}
