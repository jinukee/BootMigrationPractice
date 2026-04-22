package com.jinook.blog.command.user;

import com.jinook.blog.command.Command;
import com.jinook.blog.dao.UserDAO;
import com.jinook.blog.dto.UserDTO;
import com.jinook.blog.util.PasswordUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 회원가입 처리 Command.
 * GET → 회원가입 폼으로 포워드
 * POST → 회원가입 처리 후 로그인 페이지로 리다이렉트
 */
public class RegisterCommand implements Command {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return "/WEB-INF/views/user/register.jsp";
        }

        // POST 처리
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");

        // 이메일 중복 체크
        if (userDAO.existsByEmail(email)) {
            request.setAttribute("error", "이미 사용 중인 이메일입니다.");
            return "/WEB-INF/views/user/register.jsp";
        }

        UserDTO user = new UserDTO();
        user.setEmail(email);
        user.setPassword(PasswordUtil.hash(password));
        user.setNickname(nickname);

        int result = userDAO.insert(user);
        if (result > 0) {
            response.sendRedirect(request.getContextPath() + "/user/login.do");
            return null;
        } else {
            request.setAttribute("error", "회원가입에 실패했습니다. 다시 시도해주세요.");
            return "/WEB-INF/views/user/register.jsp";
        }
    }
}
