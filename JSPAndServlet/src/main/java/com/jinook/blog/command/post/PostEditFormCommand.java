package com.jinook.blog.command.post;

import com.jinook.blog.command.Command;
import com.jinook.blog.dao.PostDAO;
import com.jinook.blog.dto.PostDTO;
import com.jinook.blog.dto.UserDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 게시글 수정 폼 Command.
 * 작성자 본인만 수정 가능.
 */
public class PostEditFormCommand implements Command {

    private final PostDAO postDAO = new PostDAO();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.do");
            return null;
        }

        Long id = Long.parseLong(request.getParameter("id"));
        PostDTO post = postDAO.findById(id);
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

        // 작성자 본인 확인
        if (post == null || !post.getUserId().equals(loginUser.getId())) {
            response.sendRedirect(request.getContextPath() + "/post/list.do");
            return null;
        }

        request.setAttribute("post", post);
        return "/WEB-INF/views/post/edit.jsp";
    }
}
