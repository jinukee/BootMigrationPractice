package com.jinook.bootmigration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 게시글 DTO.
 * [JSP 대응]
 * JSP에서는 getter/setter/생성자를 모두 수동으로 작성했다. (약 55줄)
 * Lombok 어노테이션으로 보일러플레이트 코드를 제거한다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String authorNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
