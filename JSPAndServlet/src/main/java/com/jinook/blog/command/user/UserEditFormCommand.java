package com.jinook.blog.command.user;

import com.jinook.blog.command.Command;
import com.jinook.blog.dao.UserDAO;
import com.jinook.blog.dto.UserDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 회원정보 수정 폼 Command.
 * 세션에서 로그인 유저 정보를 가져와 수정 폼에 표시한다.
 */
public class UserEditFormCommand implements Command {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.do");
            return null;
        }

        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        // DB에서 최신 정보 조회
        UserDTO user = userDAO.findById(loginUser.getId());
        request.setAttribute("user", user);

        return "/WEB-INF/views/user/edit.jsp";
    }
}
