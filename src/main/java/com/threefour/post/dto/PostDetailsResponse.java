package com.threefour.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.threefour.post.domain.PostTimeInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDetailsResponse {

    private String authorNickname;
    private String category;
    private String title;
    private String content;
    private PostTimeInfo postTimeInfo;
    @JsonProperty("isMine")
    private boolean isMine;

    public boolean getIsMine() {
        return isMine;
    }

    public PostDetailsResponse(String authorNickname, String category, String title, String content, PostTimeInfo postTimeInfo, boolean isMine) {
        this.authorNickname = authorNickname;
        this.category = category;
        this.title = title;
        this.content = content;
        this.postTimeInfo = postTimeInfo;
        this.isMine = isMine;
    }
}
