package com.threefour.domain.comment;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "content")
    private String content;

    @Embedded
    private CommentTimeInfo commentTimeInfo;

    protected Comment() {}

    private Comment(Long userId, Long postId, String content, CommentTimeInfo commentTimeInfo) {
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.commentTimeInfo = commentTimeInfo;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }

    public CommentTimeInfo getCommentTimeInfo() {
        return commentTimeInfo;
    }

    public static Comment writeComment(Long userId, Long postId, String content) {
        return new Comment(userId, postId, content, new CommentTimeInfo(LocalDateTime.now()));
    }
}
