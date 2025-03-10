package com.threefour.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    @DisplayName("회원가입")
    void joinTest() {
        // given
        String email = "test@naver.com";
        String password = "testEncodedPassword";
        String nickname = "테스트닉네임";

        // when
        User newUser = User.join(email, password, nickname);

        // then
        assertThat(newUser).isNotNull();
        assertThat(newUser.getEmail()).isEqualTo(email);
        assertThat(newUser.getPassword()).isEqualTo(password);
        assertThat(newUser.getNickname()).isEqualTo(nickname);
        assertThat(newUser.getRole()).isEqualTo("ROLE_USER");
        assertThat(newUser.getUserTimeInfo().getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePasswordTest() {
        // given
        String email = "test@naver.com";
        String password = "testEncodedPassword";
        String nickname = "테스트닉네임";

        User user = User.join(email, password, nickname);
        String newPassword = "newPassword";

        // when
        user.changePassword(newPassword);

        // then
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    @DisplayName("닉네임 변경")
    void changeNicknameTest() {
        // given
        String email = "test@naver.com";
        String password = "testEncodedPassword";
        String nickname = "테스트닉네임";

        User user = User.join(email, password, nickname);
        String newNickname = "새로운닉네임";

        // when
        user.changeNickname(newNickname);

        // then
        assertThat(user.getNickname()).isEqualTo(newNickname);
    }

    @Test
    @DisplayName("수정일시 갱신")
    void updateUpdatedAtTest() {
        // given
        String email = "test@naver.com";
        String password = "testEncodedPassword";
        String nickname = "테스트닉네임";

        User user = User.join(email, password, nickname);
        LocalDateTime updatedAt = user.getUserTimeInfo().getUpdatedAt();

        // when
        user.updateUpdatedAt();

        // then
        assertThat(user.getUserTimeInfo().getUpdatedAt()).isNotEqualTo(updatedAt);
    }
}
