package com.threefour.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "role")
    private String role;

    @Embedded
    private UserTimeInfo userTimeInfo;

    /**
     * JwtFilter에서 사용됩니다.
     */
    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static User join(String email, String password, String nickname) {
        // password 인자는 암호화된 상태로 넘어와야 합니다.
        return new User(email, password, nickname, "ROLE_USER", new UserTimeInfo(LocalDateTime.now(), LocalDateTime.now()));
    }

    private User(String email, String password, String nickname, String role, UserTimeInfo userTimeInfo) {
        this.email = email;
        this.password = password;
        this.nickname = nickname.trim(); // 양 옆 공백 제거
        this.role = role;
        this.userTimeInfo = userTimeInfo;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname.trim(); // 양 옆 공백 제거
    }

    public void updateUpdatedAt() {
        LocalDateTime createdAt = userTimeInfo.getCreatedAt();
        this.userTimeInfo = new UserTimeInfo(createdAt, LocalDateTime.now());
    }
}