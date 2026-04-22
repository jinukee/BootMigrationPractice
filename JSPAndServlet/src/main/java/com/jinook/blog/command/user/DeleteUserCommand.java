package com.jinook.blog.command.user;

import com.jinook.blog.command.Command;
import com.jinook.blog.dao.UserDAO;
import com.jinook.blog.dto.UserDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 회원탈퇴 처리 Command.
 * 유저를 DB에서 삭제하고 세션을 무효화한다.
 * CASCADE로 인해 해당 유저의 게시글도 함께 삭제된다.
 */
public class DeleteUserCommand implements Command {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.do");
            return null;
        }

        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        int result = userDAO.deleteById(loginUser.getId());

        if (result > 0) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/post/list.do");
        return null;
    }
}
