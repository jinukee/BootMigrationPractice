package com.jinook.bootmigration.service;

import com.jinook.bootmigration.dto.UserDTO;
import com.jinook.bootmigration.entity.User;
import com.jinook.bootmigration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 유저 비즈니스 로직 서비스.
 * [Spring Security 도입 후 변경사항]
 * 1. hashPassword() (SHA-256) → PasswordEncoder.encode() (BCrypt)
 *    - SHA-256: Salt 없음, 동일 입력 → 동일 출력 (취약)
 *    - BCrypt: 자동 랜덤 Salt, 동일 입력 → 매번 다른 출력 (안전)
 * 2. login() 메서드 삭제
 *    - Before: Controller가 email/password를 받아 Service.login() 호출, 직접 비교
 *    - After:  Spring Security가 CustomUserDetailsService를 통해 자동 처리
 * 3. 의존성 변경
 *    - Before: 의존성 없음 (SHA-256은 JDK 내장)
 *    - After:  PasswordEncoder 주입받음 (SecurityConfig에서 빈 등록)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** 회원가입 */
    @Transactional
    public void register(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))  // BCrypt 해싱
                .nickname(dto.getNickname())
                .build();

        userRepository.save(user);
    }

    /** ID로 유저 조회 */
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        return toDTO(user);
    }

    /**
     * 회원정보 수정.
     * [Before] hashPassword() 직접 호출
     * [After]  passwordEncoder.encode() 사용
     */
    @Transactional
    public UserDTO updateUser(Long id, String nickname, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        String encodedPassword = (password != null && !password.isBlank())
                ? passwordEncoder.encode(password)  // BCrypt 해싱
                : null;

        user.updateProfile(nickname, encodedPassword);
        return toDTO(user);
    }

    /** 회원탈퇴 */
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // === Private Methods ===

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // login() 메서드 삭제됨
    // → Spring Security의 CustomUserDetailsService + BCryptPasswordEncoder가 대체
    // hashPassword() 메서드 삭제됨
    // → PasswordEncoder.encode()가 대체
}
