package com.jinook.bootmigration.controller;

import com.jinook.bootmigration.dto.PostDTO;
import com.jinook.bootmigration.dto.UserDTO;
import com.jinook.bootmigration.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시글 컨트롤러.
 *
 * [JSP 대응]
 * JSP에서는 6개의 Command 클래스가 게시글 관련 요청을 각각 처리했다:
 *   - PostListCommand, PostDetailCommand, PostWriteCommand
 *   - PostEditFormCommand, PostUpdateCommand, PostDeleteCommand
 *
 * Spring Boot에서는 하나의 @Controller 클래스 안에 @RequestMapping 메서드들로 통합.
 *
 * [URL 패턴 변화]
 * JSP:  /post/detail.do?id=1  →  Boot: /post/1       (@PathVariable)
 * JSP:  /post/edit.do?id=1    →  Boot: /post/1/edit   (RESTful 스타일)
 */
@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /** 게시글 목록 - JSP의 PostListCommand */
    @GetMapping("/posts")
    public String list(Model model) {
        List<PostDTO> postList = postService.findAll();
        model.addAttribute("postList", postList);
        return "post/list";
    }

    /** 게시글 상세 - JSP의 PostDetailCommand */
    @GetMapping("/post/{id}")
    public String detail(@PathVariable Long id, Model model) {
        PostDTO post = postService.findById(id);
        model.addAttribute("post", post);
        return "post/detail";
    }

    /** 게시글 작성 폼 - JSP의 PostWriteCommand (GET 분기) */
    @GetMapping("/post/write")
    public String writeForm() {
        return "post/write";
    }

    /** 게시글 작성 처리 - JSP의 PostWriteCommand (POST 분기) */
    @PostMapping("/post/write")
    public String write(@RequestParam String title,
                        @RequestParam String content,
                        HttpSession session) {
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

        PostDTO dto = PostDTO.builder()
                .title(title)
                .content(content)
                .build();

        postService.write(dto, loginUser.getId());
        return "redirect:/";
    }

    /** 게시글 수정 폼 - JSP의 PostEditFormCommand */
    @GetMapping("/post/{id}/edit")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        PostDTO post = postService.findById(id);

        // 작성자 본인 확인
        if (!post.getUserId().equals(loginUser.getId())) {
            return "redirect:/";
        }

        model.addAttribute("post", post);
        return "post/edit";
    }

    /** 게시글 수정 처리 - JSP의 PostUpdateCommand */
    @PostMapping("/post/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String content,
                         HttpSession session) {
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        postService.update(id, title, content, loginUser.getId());
        return "redirect:/post/" + id;
    }

    /** 게시글 삭제 - JSP의 PostDeleteCommand */
    @PostMapping("/post/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        postService.delete(id, loginUser.getId());
        return "redirect:/";
    }
}
