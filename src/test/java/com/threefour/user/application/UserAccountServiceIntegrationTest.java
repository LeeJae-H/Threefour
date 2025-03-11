package com.threefour.user.application;

import com.threefour.auth.AuthConstants;
import com.threefour.auth.JwtUtil;
import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.post.domain.Post;
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
import org.springframework.test.util.ReflectionTestUtils;

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
    private JwtUtil jwtUtil;

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
        String inputNickname = " 테스트닉네임 ";
        JoinRequest joinRequest = new JoinRequest(inputEmail, inputPassword, inputNickname);

        // when
        String nickname = userAccountService.join(joinRequest);

        // then
        // 1. return 값 확인
        assertThat(nickname).isNotNull();
        assertThat(nickname).isEqualTo(inputNickname.trim());

        String query = "SELECT email, password, nickname FROM user WHERE email = ?";  // email은 unique 제약 조건
        User savedUser = jdbcTemplate.queryForObject(query, new UserRowMapper(), inputEmail);

        // 2. DB에 데이터가 저장되었는지 확인
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(inputEmail);
        // 비밀번호가 암호화되었는지 확인
        assertThat(savedUser.getPassword()).isNotEqualTo(inputPassword);
        // 닉네임이 양쪽 끝의 공백을 제거된 후 저장되었는지 확인
        assertThat(savedUser.getNickname()).isEqualTo(inputNickname.trim());
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일로 회원가입 시 예외 발생")
    void join_ByExistingEmail_Then_Exception() {
        // given
        String email = "test@naver.com";
        String encodedPassword = "testEncodedPassword";
        String nickname = "테스트닉네임";
        saveUser(email, encodedPassword, nickname);

        String inputEmail = email;
        JoinRequest existingEmailRequest = new JoinRequest(inputEmail, "testPassword2", "테스트닉네임2");

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
    @DisplayName("회원 정보 수정 성공 - 모두 유효한 입력값, 모든 정보 수정")
    void updateUserInfo_ByValidInput_Then_Success() {
        // given
        String email = "test@naver.com";
        String encodedPassword = "testEncodedPassword";
        String nickname = "테스트닉네임";
        User savedUser = saveUser(email, encodedPassword, nickname);
        LocalDateTime updatedAtBefore = savedUser.getUserTimeInfo().getUpdatedAt();
        Long userId = savedUser.getId();

        String newPassword = "newPassword";
        String newNickname = " 새로운닉네임 ";
        UpdateUserInfoRequest updateRequest = new UpdateUserInfoRequest(newPassword, newNickname);

        // when
        userAccountService.updateUserInfo(userId, updateRequest, email);

        // then
        String getUserQuery = "SELECT email, password, nickname FROM user WHERE email = ?";  // email은 unique 제약 조건
        User getUser = jdbcTemplate.queryForObject(getUserQuery, new UserRowMapper(), email);
        assertThat(getUser).isNotNull();

        // 1. 비밀번호가 변경되었는지 확인 + 비밀번호가 암호화되었는지 확인
        assertThat(getUser.getPassword()).isNotEqualTo(encodedPassword);
        assertThat(getUser.getPassword()).isNotEqualTo(newPassword);

        // 2. 닉네임이 변경되었는지 확인 + 양쪽 끝의 공백을 제거된 후 저장되었는지 확인
        assertThat(getUser.getNickname()).isEqualTo(newNickname.trim());

        // 3. 수정일시가 갱신되었는지 확인
        assertThat(getUser.getUserTimeInfo().getUpdatedAt()).isNotEqualTo(updatedAtBefore);
    }

    @Test
    @DisplayName("회원 정보 수정 성공 - 모두 유효한 입력값, 비밀번호만 수정")
    void updateUserInfo_ByValidInput_OnlyPassword_Then_Success() {
        // given
        String email = "test@naver.com";
        String encodedPassword = "testEncodedPassword";
        String nickname = "테스트닉네임";
        User savedUser = saveUser(email, encodedPassword, nickname);
        LocalDateTime updatedAtBefore = savedUser.getUserTimeInfo().getUpdatedAt();
        Long userId = savedUser.getId();

        String newPassword = "newPassword";
        UpdateUserInfoRequest updateRequest = new UpdateUserInfoRequest(newPassword, null);

        // when
        userAccountService.updateUserInfo(userId, updateRequest, email);

        // then
        String getUserQuery = "SELECT email, password, nickname FROM user WHERE email = ?";
        User getUser = jdbcTemplate.queryForObject(getUserQuery, new UserRowMapper(), email);
        assertThat(getUser).isNotNull();

        // 1. 비밀번호가 변경되었는지 확인 + 비밀번호가 암호화되었는지 확인
        assertThat(getUser.getPassword()).isNotEqualTo(encodedPassword);
        assertThat(getUser.getPassword()).isNotEqualTo(newPassword);

        // 2. 닉네임은 변경되지 않았는지 확인
        assertThat(getUser.getNickname()).isEqualTo(nickname);

        // 3. 수정일시가 갱신되었는지 확인
        assertThat(getUser.getUserTimeInfo().getUpdatedAt()).isNotEqualTo(updatedAtBefore);
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 다른 사용자가 정보를 변경하려고 할 때 예외 발생")
    void updateUserInfo_FromAnotherUser_Then_Exception() {
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

        UpdateUserInfoRequest updateRequest = new UpdateUserInfoRequest("newPassword", "새로운닉네임");

        // when & then
        assertThatThrownBy(() -> userAccountService.updateUserInfo(userId, updateRequest, anotherUserEmail))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_ACCOUNT_ACCESS_DENIED);
                });
    }

    @Test
    @DisplayName("회원 탈퇴 성공 - 유효한 RefreshToken")
    void deleteUser_ByValidRefreshToken_Then_Success() {
        // given
        String email = "test@naver.com";
        String encodedPassword = "testEncodedPassword";
        String nickname = "테스트닉네임";
        User savedUser = saveUser(email, encodedPassword, nickname);
        Long userId = savedUser.getId();

        // RefreshToken
        // 생성 후 DB에 저장
        String refreshToken = jwtUtil.createJwt("refresh", email, savedUser.getRole(), AuthConstants.REFRESH_TOKEN_EXPIRATION_TIME);
        String insertQuery = "INSERT INTO refresh_token (user_email, refresh_token, expiration) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertQuery, email, refreshToken, AuthConstants.REFRESH_TOKEN_EXPIRATION_TIME);
        // 헤더 형식 맞추기
        String refreshTokenHeader = "Bearer " + refreshToken;

        // 게시글 저장
        Post post = Post.writePost(nickname, "테스트게시판", "테스트제목", "테스트내용");
        String saveQuery = "INSERT INTO post (author_nickname, category, title, content, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(saveQuery,
                post.getAuthorNickname(),
                post.getCategory(),
                post.getTitle(),
                post.getContent(),
                Timestamp.valueOf(post.getPostTimeInfo().getCreatedAt()),
                Timestamp.valueOf(post.getPostTimeInfo().getUpdatedAt())
        );

        // when
        userAccountService.deleteUser(userId, refreshTokenHeader, email);

        // then
        // 1. 회원이 작성한 게시글이 모두 삭제되었는지 확인
        String checkPostQuery = "SELECT COUNT(*) FROM post WHERE author_nickname = ?";
        Integer postCount = jdbcTemplate.queryForObject(checkPostQuery, Integer.class, nickname);
        assertThat(postCount).isEqualTo(0);;

        // 2. 회원 정보가 삭제되었는지 확인
        String checkUserQuery = "SELECT COUNT(*) FROM user WHERE email = ?";
        Integer userCount = jdbcTemplate.queryForObject(checkUserQuery, Integer.class, email);
        assertThat(userCount).isEqualTo(0);

        // 3. 해당 사용자의 모든 RefreshToken이 삭제되었는지 확인
        String checkTokenQuery = "SELECT COUNT(*) FROM refresh_token WHERE refresh_token = ?";
        Integer tokenCount = jdbcTemplate.queryForObject(checkTokenQuery, Integer.class, refreshToken);
        assertThat(tokenCount).isEqualTo(0);
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