package com.jinook.bootmigration.controller;

import com.jinook.bootmigration.config.SecurityConfig;
import com.jinook.bootmigration.dto.PostDTO;
import com.jinook.bootmigration.security.CustomUserDetails;
import com.jinook.bootmigration.security.CustomUserDetailsService;
import com.jinook.bootmigration.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PostController 슬라이스 테스트.
 *
 * [Spring Security 도입 후 변경사항]
 * - MockHttpSession → .with(user(customUserDetails))
 * - POST 요청에 .with(csrf()) 필수
 * - 비로그인 접근 시 로그인 페이지로 리다이렉트 확인
 */
@WebMvcTest(PostController.class)
@Import(SecurityConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    // === 테스트용 헬퍼 ===

    private CustomUserDetails createTestUserDetails() {
        com.jinook.bootmigration.entity.User user = com.jinook.bootmigration.entity.User.builder()
                .email("test@email.com")
                .password("encodedPassword")
                .nickname("테스터")
                .build();
        setField(user, "id", 1L);
        return new CustomUserDetails(user);
    }

    private PostDTO createTestPostDTO() {
        return PostDTO.builder()
                .id(10L)
                .title("테스트 글")
                .content("테스트 내용")
                .userId(1L)
                .authorNickname("테스터")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ============================================================
    // 게시글 목록 테스트
    // ============================================================
    @Nested
    @DisplayName("게시글 목록")
    class ListPosts {

        @Test
        @DisplayName("GET /posts → 인증된 사용자는 게시글 목록 페이지 조회 가능")
        void list_success() throws Exception {
            CustomUserDetails userDetails = createTestUserDetails();
            List<PostDTO> postList = List.of(createTestPostDTO());
            given(postService.findAll()).willReturn(postList);

            mockMvc.perform(get("/posts").with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("post/list"))
                    .andExpect(model().attributeExists("postList"));
        }

        @Test
        @DisplayName("GET /posts → 비인증 사용자는 로그인 페이지로 리다이렉트")
        void list_unauthenticated_redirectsToLogin() throws Exception {
            mockMvc.perform(get("/posts"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/login"));
        }
    }

    // ============================================================
    // 게시글 상세 테스트
    // ============================================================
    @Nested
    @DisplayName("게시글 상세")
    class Detail {

        @Test
        @DisplayName("GET /post/{id} → 인증된 사용자는 게시글 상세 조회 가능")
        void detail_success() throws Exception {
            CustomUserDetails userDetails = createTestUserDetails();
            PostDTO post = createTestPostDTO();
            given(postService.findById(10L)).willReturn(post);

            mockMvc.perform(get("/post/10").with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("post/detail"))
                    .andExpect(model().attribute("post", post))
                    .andExpect(model().attribute("loginUserId", 1L));
        }
    }

    // ============================================================
    // 게시글 작성 테스트
    // ============================================================
    @Nested
    @DisplayName("게시글 작성")
    class Write {

        @Test
        @DisplayName("GET /post/write → 작성 폼 페이지 반환")
        void writeForm_success() throws Exception {
            CustomUserDetails userDetails = createTestUserDetails();

            mockMvc.perform(get("/post/write").with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("post/write"));
        }

        @Test
        @DisplayName("POST /post/write 성공 → 홈으로 리다이렉트")
        void write_success() throws Exception {
            CustomUserDetails userDetails = createTestUserDetails();
            willDoNothing().given(postService).write(any(PostDTO.class), eq(1L));

            mockMvc.perform(post("/post/write")
                            .with(user(userDetails))
                            .with(csrf())
                            .param("title", "새 글 제목")
                            .param("content", "새 글 내용"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));
        }
    }

    // ============================================================
    // 게시글 수정 테스트
    // ============================================================
    @Nested
    @DisplayName("게시글 수정")
    class Edit {

        @Test
        @DisplayName("GET /post/{id}/edit → 수정 폼 반환 (본인 글)")
        void editForm_success() throws Exception {
            CustomUserDetails userDetails = createTestUserDetails();
            PostDTO post = createTestPostDTO();
            given(postService.findById(10L)).willReturn(post);

            mockMvc.perform(get("/post/10/edit").with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("post/edit"))
                    .andExpect(model().attribute("post", post));
        }

        @Test
        @DisplayName("GET /post/{id}/edit → 타인 글 수정 시 홈으로 리다이렉트")
        void editForm_notAuthor_redirectsToHome() throws Exception {
            CustomUserDetails userDetails = createTestUserDetails();
            PostDTO otherPost = PostDTO.builder()
                    .id(10L).title("남의 글").content("내용")
                    .userId(2L)  // 다른 유저의 글
                    .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                    .build();
            given(postService.findById(10L)).willReturn(otherPost);

            mockMvc.perform(get("/post/10/edit").with(user(userDetails)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));
        }

        @Test
        @DisplayName("POST /post/{id}/update 성공 → 해당 게시글 상세 페이지로 리다이렉트")
        void update_success() throws Exception {
            CustomUserDetails userDetails = createTestUserDetails();
            willDoNothing().given(postService).update(eq(10L), eq("수정 제목"), eq("수정 내용"), eq(1L));

            mockMvc.perform(post("/post/10/update")
                            .with(user(userDetails))
                            .with(csrf())
                            .param("title", "수정 제목")
                            .param("content", "수정 내용"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/post/10"));
        }
    }

    // ============================================================
    // 게시글 삭제 테스트
    // ============================================================
    @Nested
    @DisplayName("게시글 삭제")
    class Delete {

        @Test
        @DisplayName("POST /post/{id}/delete 성공 → 홈으로 리다이렉트")
        void delete_success() throws Exception {
            CustomUserDetails userDetails = createTestUserDetails();
            willDoNothing().given(postService).delete(10L, 1L);

            mockMvc.perform(post("/post/10/delete")
                            .with(user(userDetails))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));

            then(postService).should().delete(10L, 1L);
        }
    }

    // === Reflection 헬퍼 ===
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Reflection 필드 설정 실패: " + fieldName, e);
        }
    }
}
