package com.threefour.domain;

import com.threefour.domain.comment.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest {

    @Test
    @DisplayName("댓글 작성")
    void writeCommentTest() {
        Long userId = 1L;
        Long postId = 3L;
        String content = "테스트내용";

        // when
        Comment newComment = Comment.writeComment(userId, postId, content);

        // then
        assertThat(newComment).isNotNull();
        assertThat(newComment.getAuthor().getUserId()).isEqualTo(userId);
        assertThat(newComment.getPostId()).isEqualTo(postId);
        assertThat(newComment.getContent()).isEqualTo(content);
        assertThat(newComment.getCommentTimeInfo().getCreatedAt()).isNotNull();
    }
}
