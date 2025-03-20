package com.threefour.domain.auth;

import jakarta.persistence.*;

@Entity
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
