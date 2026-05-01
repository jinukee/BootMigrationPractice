package com.jinook.bootmigration.service;

import com.jinook.bootmigration.dto.UserDTO;
import com.jinook.bootmigration.entity.User;
import com.jinook.bootmigration.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

/**
 * UserService 단위 테스트.
 *
 * [Spring Security 도입 후 변경사항]
 * - hashPassword() (SHA-256) → PasswordEncoder.encode() (BCrypt) Mock으로 대체
 * - login() 테스트 삭제 (Spring Security가 로그인 처리를 담당)
 * - PasswordEncoder를 @Mock으로 주입
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // === 테스트용 데이터 생성 헬퍼 ===

    private User createTestUser() {
        return User.builder()
                .email("test@email.com")
                .password("$2a$10$encodedPassword")  // BCrypt 형식 해시
                .nickname("테스터")
                .build();
    }

    private UserDTO createTestUserDTO() {
        return UserDTO.builder()
                .email("test@email.com")
                .password("rawPassword123")
                .nickname("테스터")
                .build();
    }

    // ============================================================
    // 회원가입 테스트
    // ============================================================
    @Nested
    @DisplayName("회원가입")
    class Register {

        @Test
        @DisplayName("성공: 새 이메일로 회원가입하면 유저가 저장된다")
        void register_success() {
            // given
            UserDTO dto = createTestUserDTO();
            given(userRepository.existsByEmail(dto.getEmail())).willReturn(false);
            given(passwordEncoder.encode("rawPassword123")).willReturn("$2a$10$encodedPassword");

            // when
            userService.register(dto);

            // then
            then(userRepository).should().save(any(User.class));
        }

        @Test
        @DisplayName("실패: 이미 존재하는 이메일로 가입하면 예외 발생")
        void register_duplicateEmail_throwsException() {
            // given
            UserDTO dto = createTestUserDTO();
            given(userRepository.existsByEmail(dto.getEmail())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.register(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 사용 중인 이메일입니다.");

            then(userRepository).should(never()).save(any());
        }
    }

    // ============================================================
    // 유저 조회 테스트
    // ============================================================
    @Nested
    @DisplayName("유저 조회")
    class FindById {

        @Test
        @DisplayName("성공: 존재하는 ID로 조회하면 유저 정보 반환")
        void findById_success() {
            // given
            User user = createTestUser();
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            // when
            UserDTO result = userService.findById(1L);

            // then
            assertThat(result.getEmail()).isEqualTo("test@email.com");
            assertThat(result.getNickname()).isEqualTo("테스터");
        }

        @Test
        @DisplayName("실패: 존재하지 않는 ID로 조회하면 예외")
        void findById_notFound_throwsException() {
            // given
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.findById(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("유저를 찾을 수 없습니다.");
        }
    }

    // ============================================================
    // 회원정보 수정 테스트
    // ============================================================
    @Nested
    @DisplayName("회원정보 수정")
    class UpdateUser {

        @Test
        @DisplayName("성공: 닉네임과 비밀번호 모두 변경")
        void updateUser_withPassword_success() {
            // given
            User user = createTestUser();
            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(passwordEncoder.encode("newPassword")).willReturn("$2a$10$newEncodedPassword");

            // when
            UserDTO result = userService.updateUser(1L, "새닉네임", "newPassword");

            // then
            assertThat(result.getNickname()).isEqualTo("새닉네임");
        }

        @Test
        @DisplayName("성공: 비밀번호 없이 닉네임만 변경")
        void updateUser_withoutPassword_success() {
            // given
            User user = createTestUser();
            String originalPassword = user.getPassword();
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            // when
            UserDTO result = userService.updateUser(1L, "새닉네임", null);

            // then
            assertThat(result.getNickname()).isEqualTo("새닉네임");
            assertThat(result.getPassword()).isEqualTo(originalPassword);
            then(passwordEncoder).should(never()).encode(anyString());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 유저의 정보 수정 시 예외")
        void updateUser_notFound_throwsException() {
            // given
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.updateUser(999L, "닉네임", "password"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("유저를 찾을 수 없습니다.");
        }
    }

    // ============================================================
    // 회원탈퇴 테스트
    // ============================================================
    @Nested
    @DisplayName("회원탈퇴")
    class DeleteUser {

        @Test
        @DisplayName("성공: deleteById 호출 확인")
        void deleteUser_success() {
            // when
            userService.deleteUser(1L);

            // then
            then(userRepository).should().deleteById(1L);
        }
    }

    // login() 테스트 삭제됨
    // → Spring Security가 로그인을 처리하므로 UserService에 login()이 없음
    // → 로그인 테스트는 Controller 슬라이스 테스트에서 Security 통합 테스트로 수행
}
