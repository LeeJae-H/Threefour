package com.threefour.application;

import com.threefour.domain.auth.RefreshToken;
import com.threefour.domain.auth.RefreshTokenRepository;
import com.threefour.dto.auth.TokenResponse;
import com.threefour.common.Constants;
import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.domain.user.User;
import com.threefour.domain.user.UserRepository;
import com.threefour.infrastructure.auth.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public TokenResponse reissueToken(String refreshToken) {
        // 1. RefreshToken 헤더의 값이 올바른 형태인지 검증
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new ExpectedException(ErrorCode.NOT_REFRESH_TOKEN);
        }

        String token = refreshToken.split(" ")[1];

        // 2. 토큰이 RefreshToken인 지 검증
        String category = jwtProvider.getCategory(token);
        if (!category.equals("refresh")) {
            throw new ExpectedException(ErrorCode.NOT_REFRESH_TOKEN);
        }

        // 3. RefreshToken이 만료되었는 지 검증 1 -> 데이터베이스에 저장되어 있는지 여부로 확인
        if (!refreshTokenRepository.existsByRefreshToken(token)) {
            throw new ExpectedException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        // 4. RefreshToken이 만료되었는 지 검증 2 -> 토큰의 만료기간 확인
        // todo 추후 RefreshToken을 Redis에 저장한다면, 삭제해도 될 코드입니다.
        if (jwtProvider.isExpired(token)) {
            throw new ExpectedException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        String email = jwtProvider.getEmail(token);
        String role = jwtProvider.getRole(token);

        // 토큰 생성
        String newAccessToken = jwtProvider.createJwt("access", email, role, Constants.ACCESS_TOKEN_EXPIRATION_TIME);
        String newRefreshToken = jwtProvider.createJwt("refresh", email, role, Constants.REFRESH_TOKEN_EXPIRATION_TIME);

        // 데이터베이스에 존재하는 해당 RefreshToken 삭제 후 새로운 RefreshToken 저장
        refreshTokenRepository.deleteByRefreshToken(token);
        saveRefreshToken(email, newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public String getUserNickname(String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));
        return foundUser.getNickname();
    }

    private void saveRefreshToken(String email, String refresh) {
        Date date = new Date(System.currentTimeMillis() + Constants.REFRESH_TOKEN_EXPIRATION_TIME);
        RefreshToken refreshToken = new RefreshToken(email, refresh, date.toString());
        refreshTokenRepository.save(refreshToken);
    }
}
