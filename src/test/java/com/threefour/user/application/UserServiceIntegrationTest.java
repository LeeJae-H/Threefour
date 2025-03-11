package com.threefour.user.application;

import com.threefour.auth.AuthConstants;
import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.user.domain.User;
import com.threefour.user.dto.MyUserInfoResponse;
import com.threefour.user.dto.OtherUserInfoResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("truncate table user"); // 테스트 전 데이터를 초기화
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void getMyUserInfo_Success() {
        // given
        String email = "test@naver.com";
        String encodedPassword = "testEncodedPassword";
        String nickname = "테스트닉네임";
        User savedUser = saveUser(email, encodedPassword, nickname);
        Long userId = savedUser.getId();

        // when
        MyUserInfoResponse response = userService.getMyUserInfo(userId, email);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("내 정보 조회 실패 - 다른 사용자가 조회하려고 할 때 예외 발생")
    void getMyUserInfo_FromAnotherUser_Then_Exception() {
        // given
        // 사용자 저장
        String email = "test@naver.com";
        String encodedPassword = "testEncodedPassword";
        String nickname = "테스트닉네임";
        User savedUser = saveUser(email, encodedPassword, nickname);
        Long userId = savedUser.getId();

        // 사용자(다른 사용자) 저장
        String anotherUserEmail = email + "a";
        String anotherEncodedPassword = "testEncodedPassword1";
        String anotherNickname = "테스트닉네임1";
        saveUser(anotherUserEmail, anotherEncodedPassword, anotherNickname);

        // when & then
        assertThatThrownBy(() -> userService.getMyUserInfo(userId, anotherUserEmail))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_ACCOUNT_ACCESS_DENIED);
                });
    }

    @Test
    @DisplayName("다른 회원 정보 조회 성공")
    void getOtherUserInfo_Success() {
        // given
        String email = "test@naver.com";
        String encodedPassword = "testEncodedPassword";
        String nickname = "테스트닉네임";
        User savedUser = saveUser(email, encodedPassword, nickname);
        Long userId = savedUser.getId();

        // when
        OtherUserInfoResponse response = userService.getOtherUserInfo(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("다른 회원 정보 조회 실패 - 존재하지 않는 사용자 Id")
    void getOtherUserInfo_ByNotExistingUserId_Then_Exception() {
        // given
        Long notExistingUserId = 999999999L;

        // when & then
        assertThatThrownBy(() -> userService.getOtherUserInfo(notExistingUserId))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                });
    }

    private User saveUser(String email, String password, String nickname) {
        User user = User.join(email, password, nickname);

        // DB에 User 객체 저장
        String saveQuery = "INSERT INTO user (email, password, nickname, role, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(saveQuery,
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getRole(),
                Timestamp.valueOf(user.getUserTimeInfo().getCreatedAt()),
                Timestamp.valueOf(user.getUserTimeInfo().getUpdatedAt())
        );

        // DB로부터 userId 가져옴
        String getIdQuery = "SELECT id FROM user WHERE email = ?";  // email은 unique 제약 조건
        Long userId = jdbcTemplate.queryForObject(getIdQuery, Long.class, email);

        // User 객체에 userId 값 반영
        ReflectionTestUtils.setField(user, "id", userId);

        return user;
    }
}
