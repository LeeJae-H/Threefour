package com.threefour.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateUserInfoRequest {

    private String password;
    private String nickname;

    // 테스트를 위해 추가한 생성자입니다.
    public UpdateUserInfoRequest(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }
}