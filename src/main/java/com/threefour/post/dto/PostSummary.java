package com.threefour.post.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSummary {

    private Long id;
    private String title;
    private String authorNickname;
    private LocalDateTime createdAt;

    public PostSummary(Long id, String title, String authorNickname, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.authorNickname = authorNickname;
        this.createdAt = createdAt;
    }
}
