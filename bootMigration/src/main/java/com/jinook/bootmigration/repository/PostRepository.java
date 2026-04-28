package com.jinook.bootmigration.repository;

import com.jinook.bootmigration.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시글 Repository 인터페이스.
 *
 * [JSP 대응]
 * JSP에서는 PostDAO 클래스에 findAll(), findById(), insert(), update(), deleteById() 등
 * 모든 메서드를 JDBC + JOIN SQL로 직접 구현했다. (약 120줄)
 *
 * Spring Data JPA에서는 인터페이스만 선언하면 자동 구현된다.
 * - findAll() → JpaRepository 기본 제공
 * - findById() → JpaRepository 기본 제공
 * - save() → insert/update 모두 처리
 * - deleteById() → JpaRepository 기본 제공
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /** 최신순 정렬 조회 - 메서드 이름 규칙으로 ORDER BY 자동 생성 */
    List<Post> findAllByOrderByCreatedAtDesc();
}
