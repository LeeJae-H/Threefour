package com.threefour.domain.auth;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter // 테스트를 위해 추가한 어노테이션입니다.
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "expiration")
    private String expiration;

    protected RefreshToken() {}

    public RefreshToken(String userEmail, String refreshToken, String expiration) {
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }
}
