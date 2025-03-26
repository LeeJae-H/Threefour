package com.threefour.dto.comment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentListResponse {

    private List<CommentSummary> commentSummaryList;

    public CommentListResponse(List<CommentSummary> commentSummaryList) {
        this.commentSummaryList = commentSummaryList;
    }
}