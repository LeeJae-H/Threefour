package com.threefour.user.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.user.domain.User;
import com.threefour.user.dto.JoinRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
public class UserJoinServiceIntegrationTest {

    @Autowired
    private UserJoinService userJoinService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            // 생성자가 없어 정적 팩토리 메서드 사용
            return User.join(
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("nickname")
            );
        }
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("truncate table user"); // 테스트 전 데이터를 초기화
    }

    @Test
    @DisplayName("회원가입 성공 - 모두 유효한 입력값")
    void join_ByValidInput_Then_Success() {
        String inputEmail = "test@naver.com";
        String inputPassword = "testPassword";
        String inputNickname = " 테스트닉네임 ";

        // when
        String response = userJoinService.join(new JoinRequest(inputEmail, inputPassword, inputNickname));

        // then
        // 1. return 값 확인
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(inputNickname.trim());

        // 2. DB에 데이터가 저장되었는지 확인
        String query = "SELECT email, password, nickname FROM user WHERE email = ?";  // email은 unique 제약 조건
        User savedUser = jdbcTemplate.queryForObject(query, new UserRowMapper(), inputEmail);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(inputEmail);
        assertThat(savedUser.getPassword()).isNotEqualTo(inputPassword); // 비밀번호가 암호화되었는지 확인
        assertThat(savedUser.getNickname()).isEqualTo(inputNickname.trim()); // 닉네임이 양쪽 끝의 공백을 제거된 후 저장되었는지 확인
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일로 회원가입 시 예외 발생")
    void join_ByExistingEmail_Then_Exception() {
        User user = createTestUserInstance();
        String inputAlreadyUsedEmail = user.getEmail();

        // given
        // DB에 해당 이메일을 사용 중인 사용자가 존재
        saveUser(user);

        // when & then
        assertThatThrownBy(() -> userJoinService.join(new JoinRequest(inputAlreadyUsedEmail, "testPassword1", "테스트닉네임1")))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ALREADY_USED_EMAIL);
                });
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 값 검증 실패 시 예외 발생")
    void join_ByInvalidPassword_Then_Exception() {
        String inputNullPassword = null;
        String inputShortPassword = "1234";
        String inputWhiteSpacePassword = "12 34 56 78";

        // when & then
        // 1. null 값의 비밀번호
        assertThatThrownBy(() -> userJoinService.join(new JoinRequest("test@naver.com", inputNullPassword, "테스트닉네임")))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD_LENGTH);
                });
        // 2. 8자 미만인 비밀번호
        assertThatThrownBy(() -> userJoinService.join(new JoinRequest("test@naver.com", inputShortPassword, "테스트닉네임")))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD_LENGTH);
                });
        // 3. 공백이 포함된 비밀번호
        assertThatThrownBy(() -> userJoinService.join(new JoinRequest("test@naver.com", inputWhiteSpacePassword, "테스트닉네임")))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD_LENGTH);
                });
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 값 검증 실패 시 예외 발생")
    void join_ByInvalidNickname_Then_Exception() {
        String inputNullNickname = null;
        String inputShortNickname = "닉";
        String inputLongNickname = "너무너무너무너무긴닉네임";
        String inputSpecialCharNickname = "닉!네@임";
        String inputWhiteSpaceNickname = "   닉   ";
        User user = createTestUserInstance();
        String inputAlreadyUsedNickname = user.getNickname();

        // given
        // DB에 해당 닉네임을 사용 중인 사용자가 존재
        saveUser(user);

        // when & then
        // 1. null 값의 닉네임
        assertThatThrownBy(() -> userJoinService.join(new JoinRequest(user.getEmail() + "a", "testPassword", inputNullNickname)))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_NICKNAME_LENGTH);
                });
        // 2. 2자 미만인 닉네임
        assertThatThrownBy(() -> userJoinService.join(new JoinRequest(user.getEmail() + "a", "testPassword", inputShortNickname)))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_NICKNAME_LENGTH);
                });
        // 3. 10자 이상인 닉네임
        assertThatThrownBy(() -> userJoinService.join(new JoinRequest(user.getEmail() + "a", "testPassword", inputLongNickname)))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_NICKNAME_LENGTH);
                });
        // 4. 특수문자가 포함된 닉네임
        assertThatThrownBy(() -> userJoinService.join(new JoinRequest(user.getEmail() + "a", "testPassword", inputSpecialCharNickname)))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_NICKNAME_FORMAT);
                });
        // 5. 공백이 포함된 닉네임
        assertThatThrownBy(() -> userJoinService.join(new JoinRequest(user.getEmail() + "a", "testPassword", inputWhiteSpaceNickname)))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_NICKNAME_LENGTH);
                });
        // 6. 이미 사용 중인 닉네임
        assertThatThrownBy(() -> userJoinService.join(new JoinRequest(user.getEmail() + "a", "testPassword", inputAlreadyUsedNickname)))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ALREADY_USED_NICKNAME);
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
