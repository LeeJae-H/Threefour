package com.threefour.auth;

import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends Repository<RefreshToken, Long> {

    void save(RefreshToken refreshToken);
    Boolean existsByRefreshToken(String refreshToken);
    @Transactional
    void deleteByRefreshToken(String refreshToken);
    @Transactional
    void deleteByUserEmail(String userEmail);
}
