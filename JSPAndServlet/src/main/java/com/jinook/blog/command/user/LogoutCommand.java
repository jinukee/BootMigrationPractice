package com.jinook.blog.command.user;

import com.jinook.blog.command.Command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 로그아웃 처리 Command.
 * 세션을 무효화하고 게시글 목록으로 리다이렉트한다.
 */
public class LogoutCommand implements Command {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/post/list.do");
        return null;
    }
}
