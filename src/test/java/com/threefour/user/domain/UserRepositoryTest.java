package com.threefour.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 저장")
    void saveUserTest() {
        // given
        String email = "test@naver.com";
        String password = "testPassword";
        String nickname = "테스트닉네임";

        User user = User.join(email, password, nickname);

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull(); // DB에 저장됐는지 여부 확인 -> DB에 저장되지 않으면 Id는 null 값
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("Id로 사용자 조회")
    void findUserByIdTest() {
        // given
        String email = "test@naver.com";
        String password = "testPassword";
        String nickname = "테스트닉네임";

        User user = User.join(email, password, nickname);
        Long userId = userRepository.save(user).getId();

        // when
        Optional<User> foundUser = userRepository.findById(userId);

        // then
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getEmail()).isEqualTo(email);
        assertThat(foundUser.get().getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("Email로 사용자 조회")
    void findUserByEmailTest() {
        // given
        String email = "test@naver.com";
        String password = "testPassword";
        String nickname = "테스트닉네임";

        User user = User.join(email, password, nickname);
        userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByEmail(email);

        // then
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getEmail()).isEqualTo(email);
        assertThat(foundUser.get().getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("사용자 삭제")
    void deleteUserTest() {
        // given
        String email = "test@naver.com";
        String password = "testPassword";
        String nickname = "테스트닉네임";

        User user = User.join(email, password, nickname);
        Long userId = userRepository.save(user).getId();

        // when
        userRepository.delete(user);

        // then
        Optional<User> deletedUser = userRepository.findById(userId);
        boolean isExist = deletedUser.isPresent();
        assertThat(isExist).isFalse();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인")
    void existsByEmailTest() {
        // given
        String email = "test@naver.com";
        String password = "testPassword";
        String nickname = "테스트닉네임";

        User user = User.join(email, password, nickname);
        userRepository.save(user);

        String emailForTestingNotExist = "notUsed@naver.com";

        // when
        Boolean isExist = userRepository.existsByEmail(email);
        Boolean isNotExist = userRepository.existsByEmail(emailForTestingNotExist);

        // then
        assertThat(isExist).isTrue();
        assertThat(isNotExist).isFalse();
    }
}
