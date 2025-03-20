package com.threefour.application.post;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;

public class PostValidator {

    // 게시글 제목은 (양쪽 끝의 공백 제거 후) 1~50자 이내여야 한다.
    public static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty() || title.length() > 50) {
            throw new ExpectedException(ErrorCode.INVALID_TITLE_LENGTH);
        }
    }

    // 게시글 내용은 (양쪽 끝의 공백 제거 후) 최소 1자 이상이어야 한다.
    public static void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new ExpectedException(ErrorCode.INVALID_CONTENT_LENGTH);
        }
    }
}
