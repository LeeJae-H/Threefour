package com.threefour.dto.post;

import com.threefour.domain.post.Author;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSummary {

    private Long id;
    private String title;
    private Author author;
    private LocalDateTime createdAt;

    public PostSummary(Long id, String title, Author author, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.createdAt = createdAt;
    }
}
