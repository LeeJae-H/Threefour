package com.threefour.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyUserInfoResponse {

    private String email;
    private String nickname;

    public MyUserInfoResponse(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
