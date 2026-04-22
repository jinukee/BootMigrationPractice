package com.jinook.blog.command.post;

import com.jinook.blog.command.Command;
import com.jinook.blog.dao.PostDAO;
import com.jinook.blog.dto.PostDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 게시글 상세 조회 Command.
 */
public class PostDetailCommand implements Command {

    private final PostDAO postDAO = new PostDAO();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long id = Long.parseLong(request.getParameter("id"));
        PostDTO post = postDAO.findById(id);

        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/post/list.do");
            return null;
        }

        request.setAttribute("post", post);
        return "/WEB-INF/views/post/detail.jsp";
    }
}
