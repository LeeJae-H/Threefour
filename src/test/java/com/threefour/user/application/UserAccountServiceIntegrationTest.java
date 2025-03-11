package com.threefour.user.application;

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

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

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
    void join_Success() {
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
}