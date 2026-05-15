package com.jinook.csr_back.entity;

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
 *
 * [SSR → CSR 마이그레이션]
 * 기능 동등성: bootMigration의 User 엔티티와 완전히 동일한 구조.
 * Entity/Repository 계층은 SSR이든 CSR이든 DB 스키마가 동일하므로 변경 없이 재사용.
 * 변경된 것은 이 Entity를 "어떻게 외부에 노출하느냐"뿐이다:
 * - SSR: Controller → Model에 담아 Thymeleaf 템플릿으로 전달
 * - CSR: RestController → JSON DTO로 변환하여 HTTP Response Body로 전달
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
