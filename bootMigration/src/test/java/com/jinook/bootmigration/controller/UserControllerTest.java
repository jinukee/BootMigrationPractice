package com.jinook.bootmigration.controller;

import com.jinook.bootmigration.config.SecurityConfig;
import com.jinook.bootmigration.dto.UserDTO;
import com.jinook.bootmigration.security.CustomUserDetails;
import com.jinook.bootmigration.security.CustomUserDetailsService;
import com.jinook.bootmigration.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 슬라이스 테스트.
 *
 * [Spring Security 도입 후 변경사항]
 *
 * 1. MockHttpSession → @WithMockUser / .with(user(customUserDetails))
 *    - Spring Security가 세션을 관리하므로 MockHttpSession 대신 Security 테스트 도구 사용
 *
 * 2. POST 요청에 .with(csrf()) 필수
 *    - Spring Security의 CSRF 보호가 활성화되어 있으므로 테스트에서도 토큰 필요
 *
 * 3. 로그인/로그아웃 테스트 제거
 *    - Spring Security가 자동 처리하므로 Controller 단위 테스트 대상이 아님
 *
 * 4. @MockitoBean CustomUserDetailsService 추가
 *    - @WebMvcTest에서 SecurityConfig가 로드될 때 UserDetailsService 빈이 필요
 */
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    // === 테스트용 헬퍼 ===
    private CustomUserDetails createTestUserDetails() {
        // Reflection으로 ID가 설정된 User 객체 생성
        com.jinook.bootmigration.entity.User user = com.jinook.bootmigration.entity.User.builder()
                .email("test@email.com")
                .password("encodedPassword")
                .nickname("테스터")
                .build();
        setField(user, "id", 1L);
        return new CustomUserDetails(user);
    }

    // ============================================================
    // 회원가입 테스트
    // ============================================================
    @Nested
    @DisplayName("회원가입")
    class Register {

        @Test
        @DisplayName("GET /user/register → 회원가입 폼 페이지 반환 (비로그인 접근 가능)")
        void registerForm() throws Exception {
            mockMvc.perform(get("/user/register"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/register"));
        }

        @Test
        @DisplayName("POST /user/register 성공 → 로그인 페이지로 리다이렉트")
        void register_success() throws Exception {
            willDoNothing().given(userService).register(any(UserDTO.class));

            mockMvc.perform(post("/user/register")
                            .with(csrf())  // CSRF 토큰 필수
                            .param("email", "new@email.com")
                            .param("password", "password123")
                            .param("nickname", "새유저"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/login"));
        }

        @Test
        @DisplayName("POST /user/register 실패 (중복 이메일) → 에러 메시지와 함께 폼 재표시")
        void register_duplicateEmail() throws Exception {
            willThrow(new IllegalArgumentException("이미 사용 중인 이메일입니다."))
                    .given(userService).register(any(UserDTO.class));

            mockMvc.perform(post("/user/register")
                            .with(csrf())
                            .param("email", "duplicate@email.com")
                            .param("password", "password123")
                            .param("nickname", "유저"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/register"))
                    .andExpect(model().attribute("error", "이미 사용 중인 이메일입니다."));
        }
    }

    // ============================================================
    // 로그인 폼 테스트
    // ============================================================
    @Nested
    @DisplayName("로그인 폼")
    class LoginForm {

        @Test
        @DisplayName("GET /user/login → 로그인 폼 페이지 반환 (비로그인 접근 가능)")
        void loginForm() throws Exception {
            mockMvc.perform(get("/user/login"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/login"));
        }
    }

    // ============================================================
    // 회원정보 수정 테스트
    // ============================================================
    @Nested
    @DisplayName("회원정보 수정")
    class Update {

        @Test
        @DisplayName("GET /user/edit → 수정 폼 페이지 반환 (인증된 사용자)")
        void editForm_success() throws Exception {
            CustomUserDetails userDetails = createTestUserDetails();
            UserDTO user = UserDTO.builder()
                    .id(1L)
                    .email("test@email.com")
                    .nickname("테스터")
                    .build();
            given(userService.findById(1L)).willReturn(user);

            mockMvc.perform(get("/user/edit")
                            .with(user(userDetails)))  // 인증된 사용자로 요청
                    .andExpect(status().isOk())
                    .andExpect(view().name("user/edit"))
                    .andExpect(model().attributeExists("user"));
        }

        @Test
        @DisplayName("POST /user/update 성공 → 홈으로 리다이렉트")
        void update_success() throws Exception {
            CustomUserDetails userDetails = createTestUserDetails();
            UserDTO updatedUser = UserDTO.builder()
                    .id(1L)
                    .email("test@email.com")
                    .nickname("새닉네임")
                    .build();
            given(userService.updateUser(1L, "새닉네임", null)).willReturn(updatedUser);

            mockMvc.perform(post("/user/update")
                            .with(user(userDetails))
                            .with(csrf())
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
        @DisplayName("POST /user/delete → 유저 삭제 + 로그인 페이지로 리다이렉트")
        void delete_success() throws Exception {
            CustomUserDetails userDetails = createTestUserDetails();

            mockMvc.perform(post("/user/delete")
                            .with(user(userDetails))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/login"));

            then(userService).should().deleteUser(1L);
        }
    }

    // ============================================================
    // Spring Security 접근 제어 테스트
    // ============================================================
    @Nested
    @DisplayName("접근 제어 (Spring Security)")
    class AccessControl {

        @Test
        @DisplayName("비인증 사용자가 /user/edit 접근 → 로그인 페이지로 리다이렉트")
        void accessProtectedPage_withoutLogin_redirectsToLogin() throws Exception {
            mockMvc.perform(get("/user/edit"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/login"));
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
