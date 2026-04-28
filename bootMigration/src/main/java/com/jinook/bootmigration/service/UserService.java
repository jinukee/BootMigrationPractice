package com.jinook.bootmigration.service;

import com.jinook.bootmigration.dto.UserDTO;
import com.jinook.bootmigration.entity.User;
import com.jinook.bootmigration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 유저 비즈니스 로직 서비스.
 *
 * [JSP 대응]
 * JSP에서는 각 Command 클래스 안에 비즈니스 로직이 분산되어 있었다.
 * (RegisterCommand, LoginCommand, UpdateUserCommand, DeleteUserCommand 등)
 *
 * Spring Boot에서는 @Service 계층에 비즈니스 로직을 집중시켜
 * Controller는 요청/응답만 처리하고, Service가 실제 로직을 담당한다.
 *
 * [의존성 주입]
 * JSP에서는 DAO를 Command 안에서 직접 new로 생성했다.
 * Spring에서는 @RequiredArgsConstructor + final 필드로 의존성을 자동 주입받는다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /** 회원가입 */
    @Transactional
    public void register(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(hashPassword(dto.getPassword()))
                .nickname(dto.getNickname())
                .build();

        userRepository.save(user);
    }

    /** 로그인 */
    public UserDTO login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!user.getPassword().equals(hashPassword(password))) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return toDTO(user);
    }

    /** ID로 유저 조회 */
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        return toDTO(user);
    }

    /** 회원정보 수정 */
    @Transactional
    public UserDTO updateUser(Long id, String nickname, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        String hashedPassword = (password != null && !password.isBlank())
                ? hashPassword(password)
                : null;

        user.updateProfile(nickname, hashedPassword);
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

    /**
     * SHA-256 비밀번호 해싱.
     * JSP프로젝트에서의 PasswordUtil.hash()와 동일한 로직.
     * 추후 Spring Security 도입 시 BCryptPasswordEncoder로 교체 예정.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }
}
