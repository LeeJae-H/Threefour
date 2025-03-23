package com.threefour.domain;

import com.threefour.TestDatabaseConfig;
import com.threefour.domain.auth.RefreshToken;
import com.threefour.domain.auth.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestDatabaseConfig.class)
public class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("truncate table refresh_token"); // 테스트 전 데이터를 초기화
    }

    @Test
    @DisplayName("리프레시 토큰 저장")
    void saveRefreshTokenTest() {
        RefreshToken refreshToken = createTestRefreshTokenInstance();

        // when
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        // then
        assertThat(savedRefreshToken).isNotNull();
        assertThat(savedRefreshToken.getId()).isNotNull(); // DB에 저장됐는지 여부 확인 -> DB에 저장되지 않으면 Id는 null 값
        assertThat(savedRefreshToken.getUserEmail()).isEqualTo(refreshToken.getUserEmail());
        assertThat(savedRefreshToken.getRefreshToken()).isEqualTo(refreshToken.getRefreshToken());
        assertThat(savedRefreshToken.getExpiration()).isEqualTo(refreshToken.getExpiration());
    }

    @Test
    @DisplayName("리프레시 토큰 존재 여부 확인")
    void existsByRefreshTokenTest() {
        RefreshToken refreshToken = createTestRefreshTokenInstance();
        String notExistingRefreshToken = "refresh-token-is-not-existing-in-database";

        // given
        // DB에 리프레시 토큰이 존재
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        // when
        Boolean isExist = refreshTokenRepository.existsByRefreshToken(savedRefreshToken.getRefreshToken());
        Boolean isNotExist = refreshTokenRepository.existsByRefreshToken(notExistingRefreshToken);

        // then
        assertThat(isExist).isTrue();
        assertThat(isNotExist).isFalse();
    }

    @Test
    @DisplayName("리프레시 토큰 삭제")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void deleteByRefreshTokenTest() {
        RefreshToken refreshToken = createTestRefreshTokenInstance();

        // given
        // DB에 리프레시 토큰이 존재
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        // when
        refreshTokenRepository.deleteByRefreshToken(savedRefreshToken.getRefreshToken());

        // then
        String query = "SELECT COUNT(*) FROM refresh_token WHERE refresh_token = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, savedRefreshToken.getRefreshToken());
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("회원 이메일로 해당 회원의 모든 리프레시 토큰 삭제")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void deleteByUserEmailTest() {
        // given
        RefreshToken refreshToken1 = createTestRefreshTokenInstance();
        RefreshToken refreshToken2 = createTestRefreshTokenInstance();
        String userEmail = refreshToken1.getUserEmail();

        // given
        // DB에 리프레시 토큰들이 존재
        refreshTokenRepository.save(refreshToken1);
        refreshTokenRepository.save(refreshToken2);

        // when
        refreshTokenRepository.deleteByUserEmail(userEmail);

        // then
        String query = "SELECT COUNT(*) FROM refresh_token WHERE user_email = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, userEmail);
        assertThat(count).isZero();
    }

    private RefreshToken createTestRefreshTokenInstance() {
        String email = "test@naver.com";
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJlbWFpbCI6ImRsd29ndWQ3MjVAZ21haWwuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTc0Mjc0NzE2OCwiZXhwIjoxNzQyODMzNTY4fQ.JpJgVp3Snve4YqoTnXUDla8nizwa0Zl6mhf3TSFM2jk";
        String expiration = LocalDateTime.now().toString();
        return new RefreshToken(email, refreshToken, expiration);
    }
}

