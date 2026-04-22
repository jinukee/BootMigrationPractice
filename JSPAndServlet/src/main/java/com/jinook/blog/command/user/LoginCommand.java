package com.jinook.blog.command.user;

import com.jinook.blog.command.Command;
import com.jinook.blog.dao.UserDAO;
import com.jinook.blog.dto.UserDTO;
import com.jinook.blog.util.PasswordUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 로그인 처리 Command.
 * GET → 로그인 폼으로 포워드
 * POST → 로그인 처리 후 게시글 목록으로 리다이렉트
 */
public class LoginCommand implements Command {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return "/WEB-INF/views/user/login.jsp";
        }

        // POST 처리
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UserDTO user = userDAO.findByEmail(email);

        if (user == null || !PasswordUtil.matches(password, user.getPassword())) {
            request.setAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "/WEB-INF/views/user/login.jsp";
        }

        // 세션에 로그인 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);

        response.sendRedirect(request.getContextPath() + "/post/list.do");
        return null;
    }
}
