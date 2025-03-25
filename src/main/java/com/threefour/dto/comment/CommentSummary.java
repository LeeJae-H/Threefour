package com.threefour.dto.comment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentSummary {

    private String content;
    private String nickname;
    private LocalDateTime createdAt;

    public CommentSummary(String content, String nickname, LocalDateTime createdAt) {
        this.content = content;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }
}