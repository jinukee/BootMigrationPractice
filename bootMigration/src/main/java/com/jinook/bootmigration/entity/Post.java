package com.jinook.bootmigration.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 게시글 JPA 엔티티.
 * [JSP 대응]
 * JSP에서는 PostDTO가 DB 테이블과 매핑되었고,
 * PostDAO에서 JOIN SQL을 직접 작성하여 작성자 닉네임을 가져왔다.
 * JPA에서는 @ManyToOne 연관관계로 자동 조인이 이루어진다.
 */
@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id // pk로 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // pk 전략 -> IDENTITY로 설정 시 MySQL의 Auto_Increment와 동일한 효과.
    private Long id;

    @Column(nullable = false) // column 세부 설정
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * N:1 연관관계.
     * JSP에서는 user_id를 Long으로 직접 관리하고 JOIN SQL을 작성했지만,
     * JPA에서는 객체 참조로 관계를 표현한다.
     */
    @ManyToOne(fetch = FetchType.LAZY) // 하나의 user가 여러개의 post 작성 가능 -> N : 1 mapping, FetchType.LAZY option을 통해 지연로딩
    // LAZY 설정 시 'SELECT * FROM posts' 쿼리 실행 시 각각의 User를 찾기 위한 쿼리를 날리지 않고, User 자리에 Proxy(가짜 객체)를 주입시키게 됨.
    @JoinColumn(name = "user_id", nullable = false) // fk인 user_id를
    private User user;

    @CreationTimestamp // 생성 시각
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // 수정 시각
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    // 비즈니스 메서드: 게시글 수정
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
