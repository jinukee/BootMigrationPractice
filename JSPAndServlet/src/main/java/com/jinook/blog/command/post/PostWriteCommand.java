package com.jinook.blog.command.post;

import com.jinook.blog.command.Command;
import com.jinook.blog.dao.PostDAO;
import com.jinook.blog.dto.PostDTO;
import com.jinook.blog.dto.UserDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 게시글 작성 Command.
 * GET → 작성 폼으로 포워드
 * POST → 게시글 저장 후 목록으로 리다이렉트
 */
public class PostWriteCommand implements Command {

    private final PostDAO postDAO = new PostDAO();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.do");
            return null;
        }

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return "/WEB-INF/views/post/write.jsp";
        }

        // POST 처리
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        PostDTO post = new PostDTO();
        post.setTitle(title);
        post.setContent(content);
        post.setUserId(loginUser.getId());

        postDAO.insert(post);
        response.sendRedirect(request.getContextPath() + "/post/list.do");
        return null;
    }
}
