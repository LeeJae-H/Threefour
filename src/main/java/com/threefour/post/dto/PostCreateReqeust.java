package com.threefour.post.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCreateReqeust {

    private String category;
    private String title;
    private String content;
}
