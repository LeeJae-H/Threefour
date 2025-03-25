package com.threefour.domain.comment;

import com.threefour.domain.common.Author;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Author author;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "content")
    private String content;

    @Embedded
    private CommentTimeInfo commentTimeInfo;

    protected Comment() {}

    private Comment(Author author, Long postId, String content, CommentTimeInfo commentTimeInfo) {
        this.author = author;
        this.postId = postId;
        this.content = content;
        this.commentTimeInfo = commentTimeInfo;
    }

    public Long getId() {
        return id;
    }

    public Author getAuthor() {
        return author;
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
        return new Comment(new Author(userId), postId, content, new CommentTimeInfo(LocalDateTime.now()));
    }
}
