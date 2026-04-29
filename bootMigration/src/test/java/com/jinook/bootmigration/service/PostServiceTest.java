package com.jinook.bootmigration.service;

import com.jinook.bootmigration.dto.PostDTO;
import com.jinook.bootmigration.entity.Post;
import com.jinook.bootmigration.entity.User;
import com.jinook.bootmigration.repository.PostRepository;
import com.jinook.bootmigration.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * PostService 단위 테스트.
 *
 * [테스트 방식: 단위 테스트 (Unit Test)]
 * - UserServiceTest와 동일하게 Mockito로 Repository를 Mock 처리.
 * - 게시글의 CRUD + 권한 검증 로직을 테스트한다.
 *
 * [테스트 구조]
 * - @Nested: 기능별로 테스트를 그룹화하여 가독성을 높인다.
 * - @BeforeEach: 각 테스트 실행 전에 공통 테스트 데이터를 초기화한다.
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private User otherUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("author@email.com")
                .password("hashedPw")
                .nickname("작성자")
                .build();
        // Reflection으로 ID 설정 (엔티티에 setter가 없으므로)
        setField(testUser, "id", 1L);

        otherUser = User.builder()
                .email("other@email.com")
                .password("hashedPw")
                .nickname("다른유저")
                .build();
        setField(otherUser, "id", 2L);

        testPost = Post.builder()
                .title("테스트 글 제목")
                .content("테스트 글 내용")
                .user(testUser)
                .build();
        setField(testPost, "id", 10L);
    }

    // ============================================================
    // 게시글 목록 조회 테스트
    // ============================================================
    @Nested
    @DisplayName("게시글 목록 조회")
    class FindAll {

        @Test
        @DisplayName("성공: 모든 게시글을 최신순으로 조회한다")
        void findAll_success() {
            // given
            Post post2 = Post.builder().title("두번째 글").content("내용2").user(testUser).build();
            given(postRepository.findAllByOrderByCreatedAtDesc()).willReturn(List.of(testPost, post2));

            // when
            List<PostDTO> result = postService.findAll();

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTitle()).isEqualTo("테스트 글 제목");
            assertThat(result.get(0).getAuthorNickname()).isEqualTo("작성자");
        }

        @Test
        @DisplayName("성공: 게시글이 없으면 빈 리스트를 반환한다")
        void findAll_empty() {
            // given
            given(postRepository.findAllByOrderByCreatedAtDesc()).willReturn(List.of());

            // when
            List<PostDTO> result = postService.findAll();

            // then
            assertThat(result).isEmpty();
        }
    }

    // ============================================================
    // 게시글 상세 조회 테스트
    // ============================================================
    @Nested
    @DisplayName("게시글 상세 조회")
    class FindById {

        @Test
        @DisplayName("성공: 존재하는 ID로 조회하면 게시글 반환")
        void findById_success() {
            // given
            given(postRepository.findById(10L)).willReturn(Optional.of(testPost));

            // when
            PostDTO result = postService.findById(10L);

            // then
            assertThat(result.getTitle()).isEqualTo("테스트 글 제목");
            assertThat(result.getContent()).isEqualTo("테스트 글 내용");
            assertThat(result.getUserId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 ID로 조회하면 예외")
        void findById_notFound_throwsException() {
            // given
            given(postRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postService.findById(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("게시글을 찾을 수 없습니다.");
        }
    }

    // ============================================================
    // 게시글 등록 테스트
    // ============================================================
    @Nested
    @DisplayName("게시글 등록")
    class Write {

        @Test
        @DisplayName("성공: 유효한 유저가 게시글을 등록한다")
        void write_success() {
            // given
            PostDTO dto = PostDTO.builder()
                    .title("새 글")
                    .content("새 내용")
                    .build();
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

            // when
            postService.write(dto, 1L);

            // then - save()가 호출되었는지 검증
            then(postRepository).should().save(any(Post.class));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 유저가 글 작성 시 예외")
        void write_userNotFound_throwsException() {
            // given
            PostDTO dto = PostDTO.builder().title("글").content("내용").build();
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postService.write(dto, 999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("유저를 찾을 수 없습니다.");
        }
    }

    // ============================================================
    // 게시글 수정 테스트
    // ============================================================
    @Nested
    @DisplayName("게시글 수정")
    class Update {

        @Test
        @DisplayName("성공: 작성자 본인이 게시글을 수정한다")
        void update_success() {
            // given
            given(postRepository.findById(10L)).willReturn(Optional.of(testPost));

            // when
            postService.update(10L, "수정된 제목", "수정된 내용", 1L);

            // then - 엔티티의 값이 실제로 변경되었는지 확인 (JPA dirty checking 대상)
            assertThat(testPost.getTitle()).isEqualTo("수정된 제목");
            assertThat(testPost.getContent()).isEqualTo("수정된 내용");
        }

        @Test
        @DisplayName("실패: 작성자가 아닌 유저가 수정 시 예외 (권한 검증)")
        void update_notAuthor_throwsException() {
            // given
            given(postRepository.findById(10L)).willReturn(Optional.of(testPost));

            // when & then - 다른 유저(ID: 2)가 수정 시도
            assertThatThrownBy(() -> postService.update(10L, "수정", "내용", 2L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("수정 권한이 없습니다.");
        }

        @Test
        @DisplayName("실패: 존재하지 않는 게시글 수정 시 예외")
        void update_postNotFound_throwsException() {
            // given
            given(postRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postService.update(999L, "제목", "내용", 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("게시글을 찾을 수 없습니다.");
        }
    }

    // ============================================================
    // 게시글 삭제 테스트
    // ============================================================
    @Nested
    @DisplayName("게시글 삭제")
    class Delete {

        @Test
        @DisplayName("성공: 작성자 본인이 게시글을 삭제한다")
        void delete_success() {
            // given
            given(postRepository.findById(10L)).willReturn(Optional.of(testPost));

            // when
            postService.delete(10L, 1L);

            // then - delete()가 호출되었는지 검증
            then(postRepository).should().delete(testPost);
        }

        @Test
        @DisplayName("실패: 작성자가 아닌 유저가 삭제 시 예외 (권한 검증)")
        void delete_notAuthor_throwsException() {
            // given
            given(postRepository.findById(10L)).willReturn(Optional.of(testPost));

            // when & then - 다른 유저(ID: 2)가 삭제 시도
            assertThatThrownBy(() -> postService.delete(10L, 2L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("삭제 권한이 없습니다.");
        }

        @Test
        @DisplayName("실패: 존재하지 않는 게시글 삭제 시 예외")
        void delete_postNotFound_throwsException() {
            // given
            given(postRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postService.delete(999L, 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("게시글을 찾을 수 없습니다.");
        }
    }

    // === Reflection 헬퍼: @GeneratedValue로 자동 생성되는 ID를 테스트에서 수동 설정 ===
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Reflection 필드 설정 실패: " + fieldName, e);
        }
    }
}
