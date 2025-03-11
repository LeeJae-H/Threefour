package com.threefour.user.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.user.domain.User;
import com.threefour.user.dto.JoinRequest;
import org.assertj.core.api.Assertions;
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
    void join_ByAllValidInput_Then_Success() {
        // given
        String inputEmail = "test@naver.com";
        String inputPassword = "testPassword";
        String inputNickname = "테스트닉네임";
        JoinRequest joinRequest = new JoinRequest(inputEmail, inputPassword, inputNickname);

        // when
        String nickname = userAccountService.join(joinRequest);

        // then
        // 1. return 값 확인
        assertThat(nickname).isNotNull();
        assertThat(nickname).isEqualTo(inputNickname);

        String query = "SELECT email, password, nickname FROM user WHERE email = ?";  // email은 unique 제약 조건
        User savedUser = jdbcTemplate.queryForObject(query, new UserRowMapper(), inputEmail);

        // 2. DB에 데이터가 저장되었는지 확인
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(inputEmail);
        assertThat(savedUser.getNickname()).isEqualTo(inputNickname);

        // 3. 비밀번호가 암호화되었는지 확인
        String password = savedUser.getPassword();
        assertThat(password).isNotEqualTo(inputPassword);
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일로 회원가입 시 예외 발생")
    void join_ByExistingEmail_Then_Exception() {
        // given
        String inputEmail = "test@naver.com";
        String inputPassword = "testPassword";
        String inputNickname = "테스트닉네임";
        JoinRequest joinRequest = new JoinRequest(inputEmail, inputPassword, inputNickname);
        JoinRequest invalidReqeust = new JoinRequest(inputEmail, "testPassword2", "테스트닉네임2");

        userAccountService.join(joinRequest);

        // when & then
        assertThatThrownBy(() -> userAccountService.join(invalidReqeust))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ALREADY_EXIST_USER);
                });
    }
}