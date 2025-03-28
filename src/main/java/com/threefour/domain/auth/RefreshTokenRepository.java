package com.threefour.domain.auth;

import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends Repository<RefreshToken, Long> {

    RefreshToken save(RefreshToken refreshToken);
    Boolean existsByRefreshToken(String refreshToken);
    @Transactional
    void deleteByRefreshToken(String refreshToken);
    @Transactional
    void deleteByUserEmail(String userEmail);
}
