package com.threefour.user.domain;

import com.threefour.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    @DisplayName("회원가입")
    void joinTest() {
        String email = "test@naver.com";
        String password = "testPassword";
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
        assertThat(newUser.getUserTimeInfo().getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePasswordTest() {
        String email = "test@naver.com";
        String password = "testPassword";
        String nickname = "테스트닉네임";
        String newPassword = "newPassword";

        // given
        // 사용자 정보가 존재
        User user = User.join(email, password, nickname);
        LocalDateTime updatedAtBefore = user.getUserTimeInfo().getUpdatedAt();

        // when
        user.changePassword(newPassword);

        // then
        assertThat(user.getPassword()).isEqualTo(newPassword);
        assertThat(user.getUserTimeInfo().getUpdatedAt()).isNotEqualTo(updatedAtBefore);
    }

    @Test
    @DisplayName("닉네임 변경")
    void changeNicknameTest() {
        String email = "test@naver.com";
        String password = "testPassword";
        String nickname = "테스트닉네임";
        String newNickname = "새로운닉네임";

        // given
        // 사용자 정보가 존재
        User user = User.join(email, password, nickname);
        LocalDateTime updatedAtBefore = user.getUserTimeInfo().getUpdatedAt();

        // when
        user.changeNickname(newNickname);

        // then
        assertThat(user.getNickname()).isEqualTo(newNickname);
        assertThat(user.getUserTimeInfo().getUpdatedAt()).isNotEqualTo(updatedAtBefore);
    }
}
