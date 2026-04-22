package com.jinook.blog.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Command 패턴의 인터페이스.
 * 모든 비즈니스 로직 처리 클래스가 이 인터페이스를 구현한다.
 *
 * Spring Boot에서는 @Controller의 @RequestMapping 메서드가 이 역할을 대신한다.
 * → 별도의 Command 인터페이스 없이 컨트롤러 메서드가 직접 요청을 처리.
 *
 * @return forward할 JSP 경로. null이면 redirect가 이미 수행된 것으로 간주.
 */
public interface Command {
    String execute(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
