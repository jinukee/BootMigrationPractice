package com.jinook.blog.command.post;

import com.jinook.blog.command.Command;
import com.jinook.blog.dao.PostDAO;
import com.jinook.blog.dto.PostDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 게시글 목록 조회 Command.
 * 로그인 여부와 관계없이 모든 유저가 조회 가능.
 */
public class PostListCommand implements Command {

    private final PostDAO postDAO = new PostDAO();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<PostDTO> postList = postDAO.findAll();
        request.setAttribute("postList", postList);
        return "/WEB-INF/views/post/list.jsp";
    }
}
