package com.threefour.auth;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public RefreshToken(String userEmail, String refreshToken, String expiration) {
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }
}
