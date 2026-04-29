package com.jinook.bootmigration.controller;

import com.jinook.bootmigration.config.WebConfig;
import com.jinook.bootmigration.dto.PostDTO;
import com.jinook.bootmigration.dto.UserDTO;
import com.jinook.bootmigration.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PostController 슬라이스 테스트.
 *
 * [테스트 방식: 슬라이스 테스트 (Slice Test)]
 * - UserControllerTest와 동일한 방식.
 * - 게시글 CRUD의 HTTP 요청/응답 + 세션 기반 권한 처리를 검증한다.
 *
 * [핵심 학습 포인트]
 * - @PathVariable 테스트: URL 경로에 변수가 포함된 경우의 테스트 방법
 * - 세션 의존 테스트: MockHttpSession으로 로그인 상태를 시뮬레이션
 */
@WebMvcTest(PostController.class)
@Import(WebConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    // === 테스트용 헬퍼 ===

    private MockHttpSession createLoginSession() {
        MockHttpSession session = new MockHttpSession();
        UserDTO loginUser = UserDTO.builder()
                .id(1L)
                .email("test@email.com")
                .nickname("테스터")
                .build();
        session.setAttribute("loginUser", loginUser);
        return session;
    }

    private PostDTO createTestPostDTO() {
        return PostDTO.builder()
                .id(10L)
                .title("테스트 글")
                .content("테스트 내용")
                .userId(1L)
                .authorNickname("테스터")
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
    }

    // ============================================================
    // 게시글 목록 테스트
    // ============================================================
    @Nested
    @DisplayName("게시글 목록")
    class ListPosts {

        @Test
        @DisplayName("GET /posts → 게시글 목록 페이지 반환 (비로그인도 접근 가능)")
        void list_success() throws Exception {
            // given
            List<PostDTO> postList = List.of(createTestPostDTO());
            given(postService.findAll()).willReturn(postList);

            // when & then
            mockMvc.perform(get("/posts"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("post/list"))
                    .andExpect(model().attributeExists("postList"));
        }
    }

    // ============================================================
    // 게시글 상세 테스트
    // ============================================================
    @Nested
    @DisplayName("게시글 상세")
    class Detail {

        @Test
        @DisplayName("GET /post/{id} → 게시글 상세 페이지 반환 (비로그인도 접근 가능)")
        void detail_success() throws Exception {
            // given
            PostDTO post = createTestPostDTO();
            given(postService.findById(10L)).willReturn(post);

            // when & then - @PathVariable로 id를 전달
            mockMvc.perform(get("/post/10"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("post/detail"))
                    .andExpect(model().attribute("post", post));
        }
    }

    // ============================================================
    // 게시글 작성 테스트
    // ============================================================
    @Nested
    @DisplayName("게시글 작성")
    class Write {

        @Test
        @DisplayName("GET /post/write → 작성 폼 페이지 반환 (로그인 필수)")
        void writeForm_success() throws Exception {
            MockHttpSession session = createLoginSession();

            mockMvc.perform(get("/post/write").session(session))
                    .andExpect(status().isOk())
                    .andExpect(view().name("post/write"));
        }

        @Test
        @DisplayName("POST /post/write 성공 → 홈으로 리다이렉트")
        void write_success() throws Exception {
            // given
            MockHttpSession session = createLoginSession();
            willDoNothing().given(postService).write(any(PostDTO.class), eq(1L));

            // when & then
            mockMvc.perform(post("/post/write")
                            .session(session)
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
            // given - 로그인 유저 ID(1)와 게시글 작성자 ID(1)가 동일
            MockHttpSession session = createLoginSession();
            PostDTO post = createTestPostDTO(); // userId = 1L
            given(postService.findById(10L)).willReturn(post);

            // when & then
            mockMvc.perform(get("/post/10/edit").session(session))
                    .andExpect(status().isOk())
                    .andExpect(view().name("post/edit"))
                    .andExpect(model().attribute("post", post));
        }

        @Test
        @DisplayName("GET /post/{id}/edit → 타인 글 수정 시 홈으로 리다이렉트")
        void editForm_notAuthor_redirectsToHome() throws Exception {
            // given - 로그인 유저 ID(1)와 게시글 작성자 ID(2)가 다름
            MockHttpSession session = createLoginSession();
            PostDTO otherPost = PostDTO.builder()
                    .id(10L)
                    .title("남의 글")
                    .content("내용")
                    .userId(2L)  // 다른 유저의 글
                    .build();
            given(postService.findById(10L)).willReturn(otherPost);

            // when & then
            mockMvc.perform(get("/post/10/edit").session(session))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));
        }

        @Test
        @DisplayName("POST /post/{id}/update 성공 → 해당 게시글 상세 페이지로 리다이렉트")
        void update_success() throws Exception {
            // given
            MockHttpSession session = createLoginSession();
            willDoNothing().given(postService).update(eq(10L), eq("수정 제목"), eq("수정 내용"), eq(1L));

            // when & then
            mockMvc.perform(post("/post/10/update")
                            .session(session)
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
            // given
            MockHttpSession session = createLoginSession();
            willDoNothing().given(postService).delete(10L, 1L);

            // when & then
            mockMvc.perform(post("/post/10/delete").session(session))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));

            // delete가 호출되었는지 검증
            then(postService).should().delete(10L, 1L);
        }
    }
}
