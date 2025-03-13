package com.threefour.user.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.user.domain.User;
import com.threefour.user.dto.OtherUserInfoResponse;
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
    @DisplayName("다른 회원 정보 조회 성공")
    void getOtherUserInfo_Success() {
        User user = createTestUserInstance();

        // given
        // DB에 사용자가 존재
        User savedUser = saveUser(user);
        Long userId = savedUser.getId();

        // when
        OtherUserInfoResponse response = userService.getOtherUserInfo(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo(savedUser.getNickname());
    }

    @Test
    @DisplayName("다른 회원 정보 조회 실패 - 존재하지 않는 사용자 Id")
    void getOtherUserInfo_ByNotExistingUserId_Then_Exception() {
        Long notExistingUserId = 999999999L;

        // when & then
        assertThatThrownBy(() -> userService.getOtherUserInfo(notExistingUserId))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                });
    }

    private User createTestUserInstance() {
        String email = "test@naver.com";
        String encodedPassword = "testEncodedPassword";
        String nickname = "테스트닉네임"; // 처음과 끝에 공백을 넣으면 안됩니다.
        return User.join(email, encodedPassword, nickname);
    }

    private User saveUser(User user) {
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
        Long userId = jdbcTemplate.queryForObject(getIdQuery, Long.class, user.getEmail());

        // User 객체에 userId 값 반영
        ReflectionTestUtils.setField(user, "id", userId);

        return user;
    }
}
