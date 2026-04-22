package com.jinook.blog.dao;

import com.jinook.blog.dto.PostDTO;
import com.jinook.blog.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 관련 데이터베이스 접근 객체.
 * Spring Boot에서는 Spring Data JPA의 JpaRepository 인터페이스로 대체된다.
 */
public class PostDAO {

    /**
     * 게시글 목록 조회 (작성자 닉네임 포함)
     */
    public List<PostDTO> findAll() {
        String sql = "SELECT p.*, u.nickname AS author_nickname " +
                "FROM posts p JOIN users u ON p.user_id = u.id " +
                "ORDER BY p.created_at DESC";
        List<PostDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 게시글 상세 조회
     */
    public PostDTO findById(Long id) {
        String sql = "SELECT p.*, u.nickname AS author_nickname " +
                "FROM posts p JOIN users u ON p.user_id = u.id " +
                "WHERE p.id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 게시글 등록
     */
    public int insert(PostDTO post) {
        String sql = "INSERT INTO posts (title, content, user_id) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, post.getTitle());
            pstmt.setString(2, post.getContent());
            pstmt.setLong(3, post.getUserId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 게시글 수정
     */
    public int update(PostDTO post) {
        String sql = "UPDATE posts SET title = ?, content = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, post.getTitle());
            pstmt.setString(2, post.getContent());
            pstmt.setLong(3, post.getId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 게시글 삭제
     */
    public int deleteById(Long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    private PostDTO mapRow(ResultSet rs) throws SQLException {
        PostDTO post = new PostDTO();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setUserId(rs.getLong("user_id"));
        post.setAuthorNickname(rs.getString("author_nickname"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (createdAt != null) post.setCreatedAt(createdAt.toLocalDateTime());
        if (updatedAt != null) post.setUpdatedAt(updatedAt.toLocalDateTime());
        return post;
    }
}
