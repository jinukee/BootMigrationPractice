package com.jinook.bootmigration.security;

import com.jinook.bootmigration.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security의 UserDetails 구현체.
 * [왜 필요한가?]
 * Spring Security는 로그인한 사용자 정보를 UserDetails 인터페이스로 관리한다.
 * 우리의 User 엔티티를 감싸서 Spring Security가 인식할 수 있는 형태로 변환하는 어댑터(Adapter) 역할.
 * [Before: HttpSession 방식]
 * session.getAttribute("loginUser") → UserDTO 캐스팅 → loginUser.getId()
 * [After: Spring Security 방식]
 * @AuthenticationPrincipal CustomUserDetails userDetails → userDetails.getId()
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String nickname;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
    }

    /**
     * Spring Security가 사용하는 "사용자 식별자".
     * 우리 시스템에서는 email이 로그인 ID 역할을 하므로 email을 반환한다.
     */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 사용자의 권한(Role) 목록.
     * 현재는 단일 역할(ROLE_USER)만 부여하지만,
     * 추후 관리자 기능 추가 시 ROLE_ADMIN 등을 여기서 분기한다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
