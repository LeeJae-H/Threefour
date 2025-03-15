package com.threefour.post.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSummaryResponse {

    private List<PostSummary> postSummaryList;
    private int totalPages;

    public PostSummaryResponse(List<PostSummary> postSummaryList, int totalPages) {
        this.postSummaryList = postSummaryList;
        this.totalPages = totalPages;
    }
}
