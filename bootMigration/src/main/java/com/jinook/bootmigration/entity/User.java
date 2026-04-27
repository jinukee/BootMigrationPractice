package com.jinook.bootmigration.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 유저 JPA 엔티티.
 * [JSP 대응]
 * JSP에서는 UserDTO가 DB 테이블과 1:1 매핑되는 순수 자바 클래스였다.
 * Spring Boot + JPA에서는 @Entity 어노테이션으로 DB 테이블과 자동 매핑된다.
 * [Lombok 활용]
 * JSP에서 수동으로 작성했던 getter/setter/생성자를 어노테이션 하나로 대체한다.
 * - @Getter: 모든 필드의 getter 자동 생성
 * - @NoArgsConstructor: 기본 생성자 (JPA 필수)
 * - @Builder: 빌더 패턴 생성자
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    // 비즈니스 메서드: 회원정보 수정
    public void updateProfile(String nickname, String password) {
        this.nickname = nickname;
        if (password != null && !password.isBlank()) {
            this.password = password;
        }
    }
}
