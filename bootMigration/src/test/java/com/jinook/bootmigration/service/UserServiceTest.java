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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * UserService 단위 테스트.
 *
 * [테스트 방식: 단위 테스트 (Unit Test)]
 * - @ExtendWith(MockitoExtension.class): Spring 컨텍스트를 로드하지 않고 Mockito만 사용.
 *   → DB 연결 없이 순수 자바 코드만으로 테스트하므로 실행 속도가 빠르다.
 *
 * - @Mock: 가짜(Mock) 객체를 생성한다. 실제 DB에 접근하지 않는다.
 * - @InjectMocks: Mock 객체를 주입받아 테스트 대상 객체를 생성한다.
 *
 * [BDD 스타일 Mockito]
 * - given(...).willReturn(...): "이 메서드가 호출되면 이 값을 반환해라"
 * - then(...).should(): "이 메서드가 호출되었는지 검증해라"
 *
 * [AssertJ]
 * - assertThat(...).isEqualTo(...): 값이 같은지 검증
 * - assertThatThrownBy(...): 예외가 발생하는지 검증
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock // 실제 userRepository가 아닌 mock(가짜 객체 생성)
    private UserRepository userRepository;

    @InjectMocks // mock(가짜 객체)를 service에 주입
    private UserService userService;

    // === 테스트용 데이터 생성 헬퍼 ===

    private User createTestUser() {
        return User.builder()
                .email("test@email.com")
                .password("hashedPassword")
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
    @Nested // 여러개의 관련 test method를 카테고리 별로 묶어서 관리하는 방법
    @DisplayName("회원가입")
    class Register {

        @Test
        @DisplayName("성공: 새 이메일로 회원가입하면 유저가 저장된다")
        void register_success() {
            // given - 준비: 해당 이메일이 존재하지 않는 상태
            UserDTO dto = createTestUserDTO();
            given(userRepository.existsByEmail(dto.getEmail())).willReturn(false);

            // when - 실행: 회원가입 호출
            userService.register(dto);

            // then - 검증: save()가 1번 호출되었는지 확인
            then(userRepository).should().save(any(User.class));
        }

        @Test
        @DisplayName("실패: 이미 존재하는 이메일로 가입하면 예외 발생")
        void register_duplicateEmail_throwsException() {
            // given - 준비: 해당 이메일이 이미 존재하는 상태
            UserDTO dto = createTestUserDTO();
            given(userRepository.existsByEmail(dto.getEmail())).willReturn(true);

            // when & then - 실행 + 검증: 예외가 발생하고, 메시지가 일치하는지 확인
            assertThatThrownBy(() -> userService.register(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 사용 중인 이메일입니다.");

            // save()는 호출되지 않아야 한다
            then(userRepository).should(org.mockito.Mockito.never()).save(any());
        }
    }

    // ============================================================
    // 로그인 테스트
    // ============================================================
    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("성공: 올바른 이메일과 비밀번호로 로그인")
        void login_success() {
            // given - 준비: "rawPassword123"의 SHA-256 해시값을 가진 유저가 DB에 존재
            // (UserService 내부의 hashPassword와 동일한 로직으로 비밀번호가 저장되어 있어야 함)
            String rawPassword = "rawPassword123";
            String hashedPassword = hashSHA256(rawPassword);

            User user = User.builder()
                    .email("test@email.com")
                    .password(hashedPassword)
                    .nickname("테스터")
                    .build();

            given(userRepository.findByEmail("test@email.com")).willReturn(Optional.of(user));

            // when - 실행
            UserDTO result = userService.login("test@email.com", rawPassword);

            // then - 검증: 반환된 DTO의 이메일이 일치하는지 확인
            assertThat(result.getEmail()).isEqualTo("test@email.com");
            assertThat(result.getNickname()).isEqualTo("테스터");
        }

        @Test
        @DisplayName("실패: 존재하지 않는 이메일로 로그인 시 예외")
        void login_emailNotFound_throwsException() {
            // given
            given(userRepository.findByEmail("wrong@email.com")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.login("wrong@email.com", "password"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        @Test
        @DisplayName("실패: 비밀번호 불일치 시 예외")
        void login_wrongPassword_throwsException() {
            // given - 실제 비밀번호는 "correctPassword"이지만 다른 비밀번호로 시도
            User user = User.builder()
                    .email("test@email.com")
                    .password(hashSHA256("correctPassword"))
                    .nickname("테스터")
                    .build();

            given(userRepository.findByEmail("test@email.com")).willReturn(Optional.of(user));

            // when & then
            assertThatThrownBy(() -> userService.login("test@email.com", "wrongPassword"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");
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

            // when
            UserDTO result = userService.updateUser(1L, "새닉네임", "newPassword");

            // then - 닉네임이 변경되었는지 확인
            assertThat(result.getNickname()).isEqualTo("새닉네임");
        }

        @Test
        @DisplayName("성공: 비밀번호 없이 닉네임만 변경")
        void updateUser_withoutPassword_success() {
            // given
            User user = createTestUser();
            String originalPassword = user.getPassword();
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            // when - 비밀번호를 null로 전달
            UserDTO result = userService.updateUser(1L, "새닉네임", null);

            // then - 닉네임은 변경, 비밀번호는 기존 그대로
            assertThat(result.getNickname()).isEqualTo("새닉네임");
            assertThat(result.getPassword()).isEqualTo(originalPassword);
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

            // then - deleteById가 정확히 1번 호출되었는지 검증
            then(userRepository).should().deleteById(1L);
        }
    }

    // === SHA-256 해싱 헬퍼 (UserService의 private 메서드와 동일 로직) ===
    private String hashSHA256(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
