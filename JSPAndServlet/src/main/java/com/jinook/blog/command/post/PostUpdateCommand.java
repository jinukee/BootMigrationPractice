package com.jinook.blog.command.post;

import com.jinook.blog.command.Command;
import com.jinook.blog.dao.PostDAO;
import com.jinook.blog.dto.PostDTO;
import com.jinook.blog.dto.UserDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 게시글 수정 처리 Command.
 */
public class PostUpdateCommand implements Command {

    private final PostDAO postDAO = new PostDAO();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.do");
            return null;
        }

        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        Long id = Long.parseLong(request.getParameter("id"));

        // 작성자 본인 확인
        PostDTO existingPost = postDAO.findById(id);
        if (existingPost == null || !existingPost.getUserId().equals(loginUser.getId())) {
            response.sendRedirect(request.getContextPath() + "/post/list.do");
            return null;
        }

        String title = request.getParameter("title");
        String content = request.getParameter("content");

        PostDTO post = new PostDTO();
        post.setId(id);
        post.setTitle(title);
        post.setContent(content);

        postDAO.update(post);
        response.sendRedirect(request.getContextPath() + "/post/detail.do?id=" + id);
        return null;
    }
}
