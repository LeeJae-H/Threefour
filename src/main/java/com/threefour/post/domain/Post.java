package com.threefour.post.domain;

import com.threefour.user.domain.UserTimeInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_nickname")
    private String authorNickname;

    @Column(name = "category")
    private String category;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Embedded
    private PostTimeInfo postTimeInfo;

    public static Post writePost(String authorNickname, String category, String title, String content) {
        return new Post(authorNickname, category, title, content, new PostTimeInfo(LocalDateTime.now(), LocalDateTime.now()));
    }

    private Post(String authorNickname, String category, String title, String content, PostTimeInfo postTimeInfo) {
        this.authorNickname = authorNickname;
        this.category = category;
        this.title = title;
        this.content = content;
        this.postTimeInfo = postTimeInfo;
    }

    public void editTitle(String title) {
        this.title = title;
    }

    public void editContent(String content) {
        this.content = content;
    }

    public void updateUpdatedAt() {
        LocalDateTime createdAt = postTimeInfo.getCreatedAt();
        this.postTimeInfo = new PostTimeInfo(createdAt, LocalDateTime.now());
    }
}