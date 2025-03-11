package com.threefour.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("truncate table user"); // 테스트 전 데이터를 초기화
    }

    @Test
    @DisplayName("사용자 저장")
    void saveUserTest() {
        User user = createTestUserInstance();

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull(); // DB에 저장됐는지 여부 확인 -> DB에 저장되지 않으면 Id는 null 값
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    @DisplayName("Id로 사용자 조회")
    void findUserByIdTest() {
        User user = createTestUserInstance();

        // given
        // DB에 사용자가 존재
        User savedUser = userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // then
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getEmail()).isEqualTo(savedUser.getEmail());
        assertThat(foundUser.get().getNickname()).isEqualTo(savedUser.getNickname());
    }

    @Test
    @DisplayName("Email로 사용자 조회")
    void findUserByEmailTest() {
        User user = createTestUserInstance();

        // given
        // DB에 사용자가 존재
        User savedUser = userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByEmail(savedUser.getEmail());

        // then
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getEmail()).isEqualTo(savedUser.getEmail());
        assertThat(foundUser.get().getNickname()).isEqualTo(savedUser.getNickname());
    }

    @Test
    @DisplayName("사용자 삭제")
    void deleteUserTest() {
        User user = createTestUserInstance();

        // given
        // DB에 사용자가 존재
        User savedUser = userRepository.save(user);

        // when
        userRepository.delete(savedUser);

        // then
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        boolean isExist = deletedUser.isPresent();
        assertThat(isExist).isFalse();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인")
    void existsByEmailTest() {
        User user = createTestUserInstance();
        String notExistingEmail = "notUsed@naver.com";

        // given
        // DB에 사용자가 존재
        User savedUser = userRepository.save(user);

        // when
        Boolean isExist = userRepository.existsByEmail(savedUser.getEmail());
        Boolean isNotExist = userRepository.existsByEmail(notExistingEmail);

        // then
        assertThat(isExist).isTrue();
        assertThat(isNotExist).isFalse();
    }

    private User createTestUserInstance() {
        String email = "test@naver.com";
        String encodedPassword = "testEncodedPassword";
        String nickname = "테스트닉네임"; // 처음과 끝에 공백을 넣으면 안됩니다.
        return User.join(email, encodedPassword, nickname);
    }
}
