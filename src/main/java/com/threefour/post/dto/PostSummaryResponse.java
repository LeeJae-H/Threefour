package com.threefour.post.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSummaryResponse {

    private String title;
    private String authorNickname;
    private LocalDateTime createdAt;

    public PostSummaryResponse(String title, String authorNickname, LocalDateTime createdAt) {
        this.title = title;
        this.authorNickname = authorNickname;
        this.createdAt = createdAt;
    }
}
