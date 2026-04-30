package com.jinook.bootmigration.security;

import com.jinook.bootmigration.entity.User;
import com.jinook.bootmigration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security의 UserDetailsService 구현체.
 * [동작 원리]
 * 1. 사용자가 로그인 폼에서 email/password를 입력하고 제출
 * 2. Spring Security가 이 클래스의 loadUserByUsername(email)을 호출
 * 3. DB에서 해당 email의 User를 조회
 * 4. CustomUserDetails로 감싸서 반환
 * 5. Spring Security가 반환된 UserDetails의 password와 입력된 password를 BCrypt로 비교
 * 6. 일치하면 SecurityContext에 Authentication 저장 (로그인 완료)
 * [Before: UserService.login()]
 * - Controller에서 직접 email/password 받기
 * - Service에서 수동으로 hashPassword() 후 비교
 * - 성공 시 session.setAttribute("loginUser", dto)
 * [After: Spring Security]
 * - 이 클래스가 DB 조회만 담당
 * - 비밀번호 비교는 Spring Security가 BCryptPasswordEncoder로 자동 수행
 * - 성공 시 SecurityContext에 자동 저장
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * @param username Spring Security에서는 "username"이라 부르지만,
     *                 현재 service에서는 email이 로그인 식별자이므로 email이 전달됨.
     *                 (SecurityConfig에서 usernameParameter("email")로 설정)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다: " + username));

        return new CustomUserDetails(user);
    }
}
