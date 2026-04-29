package com.jinook.bootmigration.controller;

import com.jinook.bootmigration.dto.UserDTO;
import com.jinook.bootmigration.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 슬라이스 테스트.
 *
 * [테스트 방식: 슬라이스 테스트 (Slice Test)]
 * - @WebMvcTest: 컨트롤러 계층만 로드한다. (Service, Repository, DB 연결 없음)
 *   → @SpringBootTest보다 빠르고, 컨트롤러의 HTTP 요청/응답 처리만 집중 테스트.
 *
 * - MockMvc: 실제 서버를 띄우지 않고 HTTP 요청을 시뮬레이션한다.
 *   → perform(get/post): 요청 전송
 *   → andExpect(status()): HTTP 상태 코드 검증
 *   → andExpect(view()): 반환되는 뷰 이름 검증
 *   → andExpect(model()): Model에 담긴 데이터 검증
 *   → andExpect(redirectedUrl()): 리다이렉트 URL 검증
 *
 * - @MockitoBean: Spring 컨텍스트에 Mock 객체를 빈으로 등록한다.
 *   (단위 테스트의 @Mock과 달리, Spring 빈 컨테이너에 주입됨)
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    // === 테스트용 세션 헬퍼 ===
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

    // ============================================================
    // 회원가입 테스트
    // ============================================================
    @Nested
    @DisplayName("회원가입")
    class Register {

        @Test
        @DisplayName("GET /user/register → 회원가입 폼 페이지 반환")
        void registerForm() throws Exception {
            mockMvc.perform(get("/user/register"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/register"));
        }

        @Test
        @DisplayName("POST /user/register 성공 → 로그인 페이지로 리다이렉트")
        void register_success() throws Exception {
            // given - register()가 정상 실행되도록 설정
            willDoNothing().given(userService).register(any(UserDTO.class));

            // when & then
            mockMvc.perform(post("/user/register")
                            .param("email", "new@email.com")
                            .param("password", "password123")
                            .param("nickname", "새유저"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/login"));
        }

        @Test
        @DisplayName("POST /user/register 실패 (중복 이메일) → 에러 메시지와 함께 폼 재표시")
        void register_duplicateEmail() throws Exception {
            // given - register()가 예외를 던지도록 설정
            willThrow(new IllegalArgumentException("이미 사용 중인 이메일입니다."))
                    .given(userService).register(any(UserDTO.class));

            // when & then
            mockMvc.perform(post("/user/register")
                            .param("email", "duplicate@email.com")
                            .param("password", "password123")
                            .param("nickname", "유저"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/register"))
                    .andExpect(model().attribute("error", "이미 사용 중인 이메일입니다."));
        }
    }

    // ============================================================
    // 로그인 테스트
    // ============================================================
    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("GET /user/login → 로그인 폼 페이지 반환")
        void loginForm() throws Exception {
            mockMvc.perform(get("/user/login"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/login"));
        }

        @Test
        @DisplayName("POST /user/login 성공 → 홈으로 리다이렉트 + 세션에 유저 저장")
        void login_success() throws Exception {
            // given
            UserDTO loginUser = UserDTO.builder()
                    .id(1L)
                    .email("test@email.com")
                    .nickname("테스터")
                    .build();
            given(userService.login("test@email.com", "password123")).willReturn(loginUser);

            // when & then
            mockMvc.perform(post("/user/login")
                            .param("email", "test@email.com")
                            .param("password", "password123"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(request().sessionAttribute("loginUser", loginUser));
        }

        @Test
        @DisplayName("POST /user/login 실패 → 에러 메시지와 함께 폼 재표시")
        void login_failure() throws Exception {
            // given
            given(userService.login("wrong@email.com", "wrongPw"))
                    .willThrow(new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

            // when & then
            mockMvc.perform(post("/user/login")
                            .param("email", "wrong@email.com")
                            .param("password", "wrongPw"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/login"))
                    .andExpect(model().attribute("error", "이메일 또는 비밀번호가 올바르지 않습니다."));
        }
    }

    // ============================================================
    // 로그아웃 테스트
    // ============================================================
    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @Test
        @DisplayName("GET /user/logout → 세션 무효화 후 홈으로 리다이렉트")
        void logout_success() throws Exception {
            MockHttpSession session = createLoginSession();

            mockMvc.perform(get("/user/logout").session(session))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));

            // 세션이 무효화되었는지 확인
            org.assertj.core.api.Assertions.assertThat(session.isInvalid()).isTrue();
        }
    }

    // ============================================================
    // 회원정보 수정 테스트
    // ============================================================
    @Nested
    @DisplayName("회원정보 수정")
    class Update {

        @Test
        @DisplayName("GET /user/edit → 수정 폼 페이지 반환 (로그인 상태)")
        void editForm_success() throws Exception {
            // given
            MockHttpSession session = createLoginSession();
            UserDTO user = UserDTO.builder()
                    .id(1L)
                    .email("test@email.com")
                    .nickname("테스터")
                    .build();
            given(userService.findById(1L)).willReturn(user);

            // when & then
            mockMvc.perform(get("/user/edit").session(session))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/edit"))
                    .andExpect(model().attributeExists("user"));
        }

        @Test
        @DisplayName("POST /user/update 성공 → 홈으로 리다이렉트")
        void update_success() throws Exception {
            // given
            MockHttpSession session = createLoginSession();
            UserDTO updatedUser = UserDTO.builder()
                    .id(1L)
                    .email("test@email.com")
                    .nickname("새닉네임")
                    .build();
            given(userService.updateUser(1L, "새닉네임", null)).willReturn(updatedUser);

            // when & then
            mockMvc.perform(post("/user/update")
                            .session(session)
                            .param("nickname", "새닉네임"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));
        }
    }

    // ============================================================
    // 회원탈퇴 테스트
    // ============================================================
    @Nested
    @DisplayName("회원탈퇴")
    class Delete {

        @Test
        @DisplayName("POST /user/delete → 유저 삭제 + 세션 무효화 + 홈으로 리다이렉트")
        void delete_success() throws Exception {
            MockHttpSession session = createLoginSession();

            mockMvc.perform(post("/user/delete").session(session))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));

            // deleteUser가 호출되었는지 검증
            then(userService).should().deleteUser(1L);
            // 세션이 무효화되었는지 확인
            org.assertj.core.api.Assertions.assertThat(session.isInvalid()).isTrue();
        }
    }

    // ============================================================
    // 인터셉터 (비로그인 접근 차단) 테스트
    // ============================================================
    @Nested
    @DisplayName("로그인 체크 인터셉터")
    class LoginInterceptor {

        @Test
        @DisplayName("비로그인 상태로 /user/edit 접근 → 로그인 페이지로 리다이렉트")
        void accessProtectedPage_withoutLogin_redirectsToLogin() throws Exception {
            mockMvc.perform(get("/user/edit"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/login"));
        }
    }
}
