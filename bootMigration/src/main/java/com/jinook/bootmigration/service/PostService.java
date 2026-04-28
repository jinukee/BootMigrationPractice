package com.jinook.bootmigration.service;

import com.jinook.bootmigration.dto.PostDTO;
import com.jinook.bootmigration.entity.Post;
import com.jinook.bootmigration.entity.User;
import com.jinook.bootmigration.repository.PostRepository;
import com.jinook.bootmigration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글 비즈니스 로직 서비스.
 *
 * [JSP 대응]
 * JSP에서는 PostListCommand, PostWriteCommand, PostUpdateCommand, PostDeleteCommand 등
 * 각 Command에 비즈니스 로직이 분산되어 있었다.
 *
 * Spring Boot에서는 @Service 계층에 모든 게시글 관련 로직을 집중시킨다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /** 게시글 목록 조회 */
    public List<PostDTO> findAll() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** 게시글 상세 조회 */
    public PostDTO findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return toDTO(post);
    }

    /** 게시글 등록 */
    @Transactional
    public void write(PostDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .build();

        postRepository.save(post);
    }

    /** 게시글 수정 */
    @Transactional
    public void update(Long postId, String title, String content, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 작성자 본인 확인
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        post.update(title, content);
    }

    /** 게시글 삭제 */
    @Transactional
    public void delete(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 작성자 본인 확인
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    // === Private Methods ===

    private PostDTO toDTO(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .userId(post.getUser().getId())
                .authorNickname(post.getUser().getNickname())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
