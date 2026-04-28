package com.jinook.bootmigration.repository;

import com.jinook.bootmigration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 유저 Repository 인터페이스.
 *
 * [JSP 대응]
 * JSP에서는 UserDAO 클래스에 findByEmail(), findById(), insert(), update(), deleteById() 등
 * 모든 메서드를 JDBC로 직접 구현했다. (약 130줄)
 *
 * Spring Data JPA에서는 인터페이스만 선언하면 구현체가 자동 생성된다.
 * → findById(), save(), deleteById() 등은 JpaRepository에 이미 포함.
 * → findByEmail()처럼 커스텀 쿼리도 메서드 이름 규칙만 따르면 자동 생성.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** 이메일로 유저 조회 - 메서드 이름만으로 쿼리 자동 생성 */
    Optional<User> findByEmail(String email);

    /** 이메일 중복 체크 */
    boolean existsByEmail(String email);
}
