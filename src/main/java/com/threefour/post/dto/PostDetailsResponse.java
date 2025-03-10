package com.threefour.post.dto;

import com.threefour.post.domain.PostTimeInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDetailsResponse {

    private String title;
    private String content;
    private String authorNickname;
    private PostTimeInfo postTimeInfo;
    private Long userId;
    private boolean isMine;

    public PostDetailsResponse(String title, String content, String authorNickname, PostTimeInfo postTimeInfo, Long userId, boolean isMine) {
        this.title = title;
        this.content = content;
        this.authorNickname = authorNickname;
        this.postTimeInfo = postTimeInfo;
        this.userId = userId;
        this.isMine = isMine;
    }
}
