package com.jinook.blog.controller;

import com.jinook.blog.command.Command;
import com.jinook.blog.command.post.*;
import com.jinook.blog.command.user.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * FrontController 서블릿.
 * 모든 *.do 요청을 받아 URL 패턴에 따라 적절한 Command 객체로 분배한다.
 * <p>
 * [Spring Boot 대응]
 * Spring Boot에서는 DispatcherServlet이 이 역할을 자동으로 수행한다.
 * → @Controller + @RequestMapping 어노테이션 기반으로 요청이 자동 매핑됨
 * → 개발자가 직접 URL-Command 매핑 코드를 작성할 필요가 없게 된다.
 */
public class FrontControllerServlet extends HttpServlet {

    private final Map<String, Command> commandMap = new HashMap<>();

    @Override
    public void init() throws ServletException {
        // URL 패턴과 Command 객체 매핑 등록
        // Spring Boot에서는 @GetMapping, @PostMapping 어노테이션이 이 역할을 대신함.

        // === 유저 관련 ===
        commandMap.put("/user/register.do", new RegisterCommand());
        commandMap.put("/user/login.do", new LoginCommand());
        commandMap.put("/user/logout.do", new LogoutCommand());
        commandMap.put("/user/edit.do", new UserEditFormCommand());
        commandMap.put("/user/update.do", new UpdateUserCommand());
        commandMap.put("/user/delete.do", new DeleteUserCommand());

        // === 게시글 관련 ===
        commandMap.put("/post/list.do", new PostListCommand());
        commandMap.put("/post/detail.do", new PostDetailCommand());
        commandMap.put("/post/write.do", new PostWriteCommand());
        commandMap.put("/post/edit.do", new PostEditFormCommand());
        commandMap.put("/post/update.do", new PostUpdateCommand());
        commandMap.put("/post/delete.do", new PostDeleteCommand());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 요청 URI에서 컨텍스트 경로를 제외한 경로 추출
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String commandKey = uri.substring(contextPath.length());

        Command command = commandMap.get(commandKey);

        if (command == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "요청한 페이지를 찾을 수 없습니다.");
            return;
        }

        try {
            String viewPath = command.execute(request, response);

            // viewPath가 null이면 Command 내부에서 이미 redirect 처리한 것
            if (viewPath != null) {
                RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
                dispatcher.forward(request, response);
            }
        } catch (Exception e) {
            throw new ServletException("요청 처리 중 오류가 발생했습니다.", e);
        }
    }
}
