package com.threefour.dto.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSummary {

    private Long postId;
    private String title;
    private String nickname;
    private LocalDateTime createdAt;

    public PostSummary(Long postId, String title, String nickname, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }
}
