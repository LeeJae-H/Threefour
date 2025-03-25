package com.threefour.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.threefour.domain.post.PostTimeInfo;
import com.threefour.dto.comment.CommentSummary;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<CommentSummary> comments;

    public boolean getIsMine() {
        return isMine;
    }

    public PostDetailsResponse(String nickname, String category, String title, String content, PostTimeInfo postTimeInfo, boolean isMine, List<CommentSummary> comments) {
        this.nickname = nickname;
        this.category = category;
        this.title = title;
        this.content = content;
        this.postTimeInfo = postTimeInfo;
        this.isMine = isMine;
        this.comments = comments;
    }
}
