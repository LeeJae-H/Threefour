package com.threefour.dto.post;

import com.threefour.domain.common.Author;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSummary {

    private Long id;
    private String title;
    private String nickname;
    private LocalDateTime createdAt;

    public PostSummary(Long id, String title, String nickname, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }
}
