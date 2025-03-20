package com.threefour.dto.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinRequest {

    private String email;
    private String password;
    private String nickname;

    // 테스트를 위해 추가한 생성자입니다.
    public JoinRequest(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
