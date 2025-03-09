package com.threefour.auth;

import org.springframework.data.repository.Repository;

public interface RefreshTokenRepository extends Repository<RefreshToken, Long> {

    void save(RefreshToken refreshToken);
    Boolean existsByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
}
