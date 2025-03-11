package com.threefour.post.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EditPostRequest {

    private String title;
    private String content;

    // 테스트를 위해 추가한 생성자입니다.
    public EditPostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
