package com.jinook.blog.dto;

import java.time.LocalDateTime;

/**
 * 게시글 데이터 전송 객체.
 * Spring Boot에서는 Lombok의 @Getter, @Setter 등으로 보일러플레이트를 줄인다.
 */
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String authorNickname; // JOIN 결과를 담기 위한 필드
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostDTO() {}

    public PostDTO(Long id, String title, String content, Long userId,
                   String authorNickname, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.authorNickname = authorNickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAuthorNickname() { return authorNickname; }
    public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
