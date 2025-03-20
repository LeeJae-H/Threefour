package com.threefour.dto.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OtherUserInfoResponse {

    private String nickname;

    public OtherUserInfoResponse(String nickname) {
        this.nickname = nickname;
    }
}
