package com.jinook.bootmigration.controller;

import com.jinook.bootmigration.dto.PostDTO;
import com.jinook.bootmigration.security.CustomUserDetails;
import com.jinook.bootmigration.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시글 컨트롤러.
 * [Spring Security 도입 후 변경사항]
 * 로그인 유저 획득 방식:
 * - Before: HttpSession session → (UserDTO) session.getAttribute("loginUser") → dto.getId()
 * - After:  @AuthenticationPrincipal CustomUserDetails userDetails → userDetails.getId()
 * 접근 제어:
 * - Before: LoginCheckInterceptor가 세션 확인
 * - After:  SecurityFilterChain이 자동으로 인증 확인 (Controller 코드에는 인증 로직 없음)
 */
@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /** 게시글 목록 */
    @GetMapping("/posts")
    public String list(Model model) {
        List<PostDTO> postList = postService.findAll();
        model.addAttribute("postList", postList);
        return "post/list";
    }

    /** 게시글 상세 */
    @GetMapping("/post/{id}")
    public String detail(@PathVariable Long id,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model) {
        PostDTO post = postService.findById(id);
        model.addAttribute("post", post);
        model.addAttribute("loginUserId", userDetails.getId());
        return "post/detail";
    }

    /** 게시글 작성 폼 */
    @GetMapping("/post/write")
    public String writeForm() {
        return "post/write";
    }

    /** 게시글 작성 처리 */
    @PostMapping("/post/write")
    public String write(@RequestParam String title,
                        @RequestParam String content,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostDTO dto = PostDTO.builder()
                .title(title)
                .content(content)
                .build();

        postService.write(dto, userDetails.getId());
        return "redirect:/";
    }

    /** 게시글 수정 폼 */
    @GetMapping("/post/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        PostDTO post = postService.findById(id);

        // 작성자 본인 확인
        if (!post.getUserId().equals(userDetails.getId())) {
            return "redirect:/";
        }

        model.addAttribute("post", post);
        return "post/edit";
    }

    /** 게시글 수정 처리 */
    @PostMapping("/post/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String content,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.update(id, title, content, userDetails.getId());
        return "redirect:/post/" + id;
    }

    /** 게시글 삭제 */
    @PostMapping("/post/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.delete(id, userDetails.getId());
        return "redirect:/";
    }
}
