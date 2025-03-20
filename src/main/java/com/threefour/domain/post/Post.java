package com.threefour.domain.post;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Author author;

    @Column(name = "category")
    private String category;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Embedded
    private PostTimeInfo postTimeInfo;

    protected Post() {}

    private Post(Author author, String category, String title, String content, PostTimeInfo postTimeInfo) {
        this.author = author;
        this.category = category;
        this.title = title;
        this.content = content;
        this.postTimeInfo = postTimeInfo;
    }

    public Long getId() {
        return id;
    }

    public Author getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public PostTimeInfo getPostTimeInfo() {
        return postTimeInfo;
    }

    public static Post writePost(String author, String category, String title, String content) {
        return new Post(new Author(author), category, title, content, new PostTimeInfo(LocalDateTime.now(), LocalDateTime.now()));
    }

    public void editTitle(String title) {
        this.title = title;
        updateUpdatedAt();
    }

    public void editContent(String content) {
        this.content = content;
        updateUpdatedAt();
    }

    private void updateUpdatedAt() {
        LocalDateTime createdAt = postTimeInfo.getCreatedAt();
        this.postTimeInfo = new PostTimeInfo(createdAt, LocalDateTime.now());
    }
}