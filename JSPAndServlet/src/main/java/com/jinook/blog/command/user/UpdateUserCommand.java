package com.jinook.blog.command.user;

import com.jinook.blog.command.Command;
import com.jinook.blog.dao.UserDAO;
import com.jinook.blog.dto.UserDTO;
import com.jinook.blog.util.PasswordUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 회원정보 수정 처리 Command.
 * 닉네임, 비밀번호를 수정한다.
 */
public class UpdateUserCommand implements Command {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.do");
            return null;
        }

        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");

        UserDTO updateUser = new UserDTO();
        updateUser.setId(loginUser.getId());
        updateUser.setNickname(nickname);

        // 비밀번호가 입력된 경우에만 변경
        if (password != null && !password.isBlank()) {
            updateUser.setPassword(PasswordUtil.hash(password));
        } else {
            updateUser.setPassword(loginUser.getPassword());
        }

        int result = userDAO.update(updateUser);
        if (result > 0) {
            // 세션 정보 갱신
            UserDTO updatedUser = userDAO.findById(loginUser.getId());
            session.setAttribute("loginUser", updatedUser);
            response.sendRedirect(request.getContextPath() + "/post/list.do");
            return null;
        } else {
            request.setAttribute("error", "회원정보 수정에 실패했습니다.");
            request.setAttribute("user", loginUser);
            return "/WEB-INF/views/user/edit.jsp";
        }
    }
}
