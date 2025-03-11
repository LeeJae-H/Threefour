package com.threefour.user.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.user.domain.User;
import com.threefour.user.dto.JoinRequest;
import com.threefour.user.dto.UpdateUserInfoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
public class UserAccountServiceIntegrationTest {

    @Autowired
    private UserAccountService userAccountService;

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
        // given
        String inputEmail = "test@naver.com";
        String inputPassword = "testPassword";
        String inputNickname = "테스트닉네임";
        JoinRequest joinRequest = new JoinRequest(inputEmail, inputPassword, inputNickname);

        // when
        String nickname = userAccountService.join(joinRequest);

        // then
        // return 값 확인
        assertThat(nickname).isNotNull();
        assertThat(nickname).isEqualTo(inputNickname);

        String query = "SELECT email, password, nickname FROM user WHERE email = ?";  // email은 unique 제약 조건
        User savedUser = jdbcTemplate.queryForObject(query, new UserRowMapper(), inputEmail);

        // DB에 데이터가 저장되었는지 확인
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(inputEmail);
        assertThat(savedUser.getNickname()).isEqualTo(inputNickname);

        // 비밀번호가 암호화되었는지 확인
        String password = savedUser.getPassword();
        assertThat(password).isNotEqualTo(inputPassword);
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일로 회원가입 시 예외 발생")
    void join_ByExistingEmail_Then_Exception() {
        // given
        String inputEmail = "test@naver.com";
        String inputExistingEmail = inputEmail;
        JoinRequest joinRequest = new JoinRequest(inputEmail, "testPassword1", "테스트닉네임1");
        JoinRequest existingEmailRequest = new JoinRequest(inputExistingEmail, "testPassword2", "테스트닉네임2");

        userAccountService.join(joinRequest);

        // when & then
        assertThatThrownBy(() -> userAccountService.join(existingEmailRequest))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ALREADY_EXIST_USER);
                });
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 값 검증 실패 시 예외 발생")
    void join_ByInvalidPassword_Then_Exception() {
        // given
        String inputNullPassword = null;
        String inputShortPassword = "1234";
        String inputWhiteSpacePassword = "12 34 56 78";
        JoinRequest nullPasswordRequest = new JoinRequest("test@naver.com", inputNullPassword, "테스트닉네임");
        JoinRequest shortPasswordRequest = new JoinRequest("test@naver.com", inputShortPassword, "테스트닉네임");
        JoinRequest whitespacePasswordRequest = new JoinRequest("test@naver.com", inputWhiteSpacePassword, "테스트닉네임");

        // when & then
        // 1. null 값의 비밀번호
        assertThatThrownBy(() -> userAccountService.join(nullPasswordRequest))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD_LENGTH);
                });
        // 2. 8자 미만인 비밀번호
        assertThatThrownBy(() -> userAccountService.join(shortPasswordRequest))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD_LENGTH);
                });
        // 3. 공백이 포함된 비밀번호
        assertThatThrownBy(() -> userAccountService.join(whitespacePasswordRequest))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD_LENGTH);
                });
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 값 검증 실패 시 예외 발생")
    void join_ByInvalidNickname_Then_Exception() {
        // given
        String inputNullNickname = null;
        String inputShortNickname = "닉";
        String inputLongNickname = "너무너무너무너무긴닉네임";
        String inputSpecialCharNickname = "닉!네@임";
        String inputWhiteSpaceNickname = "   닉   ";
        JoinRequest inputNullNicknameRequest = new JoinRequest("test@naver.com", "testPassword", inputNullNickname);
        JoinRequest inputShortNicknameRequest = new JoinRequest("test@naver.com", "testPassword", inputShortNickname);
        JoinRequest inputLongNicknameRequest = new JoinRequest("test@naver.com", "testPassword", inputLongNickname);
        JoinRequest inputSpecialCharNicknameRequest = new JoinRequest("test@naver.com", "testPassword", inputSpecialCharNickname);
        JoinRequest inputWhiteSpaceNicknameRequest = new JoinRequest("test@naver.com", "testPassword", inputWhiteSpaceNickname);

        // when & then
        // 1. null 값의 닉네임
        assertThatThrownBy(() -> userAccountService.join(inputNullNicknameRequest))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_NICKNAME_LENGTH);
                });
        // 2. 2자 미만인 닉네임
        assertThatThrownBy(() -> userAccountService.join(inputShortNicknameRequest))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_NICKNAME_LENGTH);
                });
        // 3. 10자 이상인 닉네임
        assertThatThrownBy(() -> userAccountService.join(inputLongNicknameRequest))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_NICKNAME_LENGTH);
                });
        // 4. 특수문자가 포함된 닉네임
        assertThatThrownBy(() -> userAccountService.join(inputSpecialCharNicknameRequest))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_NICKNAME_FORMAT);
                });
        // 5. 공백이 포함된 닉네임
        assertThatThrownBy(() -> userAccountService.join(inputWhiteSpaceNicknameRequest))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_NICKNAME_LENGTH);
                });
    }

    @Test
    @DisplayName("회원 정보 수정 성공 - 모두 유효한 입력값")
    void updateUserInfo_ByValidInput_Then_Success() {
        // given
        // 사용자 저장
        String email = "test@naver.com";
        String encodedPassword = "testEncodedPassword";
        String nickname = "테스트닉네임";
        User user = User.join(email, encodedPassword, nickname);
        String saveQuery = "INSERT INTO user (email, password, nickname, role, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(saveQuery,
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getRole(),
                Timestamp.valueOf(user.getUserTimeInfo().getCreatedAt()),
                Timestamp.valueOf(user.getUserTimeInfo().getUpdatedAt())
        );
        String getIdQuery = "SELECT id FROM user WHERE email = ?";  // email은 unique 제약 조건
        Long userId = jdbcTemplate.queryForObject(getIdQuery, Long.class, email);

        String newPassword = "newPassword";
        String newNickname = "새로운닉네임";
        UpdateUserInfoRequest updateRequest = new UpdateUserInfoRequest(newPassword, newNickname);

        LocalDateTime updatedAtBefore = user.getUserTimeInfo().getUpdatedAt();

        // when
        userAccountService.updateUserInfo(userId, updateRequest, email);

        // then
        String getUserQuery = "SELECT email, password, nickname FROM user WHERE email = ?";  // email은 unique 제약 조건
        User savedUser = jdbcTemplate.queryForObject(getUserQuery, new UserRowMapper(), email);
        assertThat(savedUser).isNotNull();

        // 1. 비밀번호가 변경되었는지 확인 + 비밀번호가 암호화되었는지 확인
        assertThat(savedUser.getPassword()).isNotEqualTo(encodedPassword);
        assertThat(savedUser.getPassword()).isNotEqualTo(newPassword);

        // 2. 닉네임이 변경되었는지 확인
        assertThat(savedUser.getNickname()).isEqualTo(newNickname);

        // 3. 수정일시가 갱신되었는지 확인
        assertThat(savedUser.getUserTimeInfo().getUpdatedAt()).isNotEqualTo(updatedAtBefore);
    }
}