package com.threefour.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyInfoResponse {

    private String email;
    private String nickname;

    public MyInfoResponse(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
