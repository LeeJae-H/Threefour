package com.threefour.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.threefour.domain.common.Author;
import com.threefour.domain.post.PostTimeInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDetailsResponse {

    private String nickname;
    private String category;
    private String title;
    private String content;
    private PostTimeInfo postTimeInfo;
    @JsonProperty("isMine")
    private boolean isMine;

    public boolean getIsMine() {
        return isMine;
    }

    public PostDetailsResponse(String nickname, String category, String title, String content, PostTimeInfo postTimeInfo, boolean isMine) {
        this.nickname = nickname;
        this.category = category;
        this.title = title;
        this.content = content;
        this.postTimeInfo = postTimeInfo;
        this.isMine = isMine;
    }
}
