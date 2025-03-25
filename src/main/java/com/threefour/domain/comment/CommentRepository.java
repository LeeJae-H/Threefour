package com.threefour.domain.comment;

import com.threefour.dto.comment.CommentSummary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends Repository<Comment, Long> {
    Comment save(Comment comment);
    Optional<Comment> findById(Long id);
    void delete(Comment comment);

    @Query("""
            SELECT new com.threefour.dto.comment.CommentSummary(
                c.content, u.nickname, c.commentTimeInfo.createdAt
            )
            FROM Comment c JOIN User u ON c.author.userId = u.id
            WHERE c.postId = :postId
            ORDER BY c.id ASC
            """)
    List<CommentSummary> findCommentSummary(Long postId);
}
