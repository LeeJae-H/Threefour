package com.threefour.auth;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.user.domain.User;
import com.threefour.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public TokenDTO reissueToken(String refreshToken) {
        // RefreshToken 헤더의 값이 유효한 지 검증
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new ExpectedException(ErrorCode.INVALID_REFRESH_TOKEN_FORMAT);
        }

        String token = refreshToken.split(" ")[1];

        // 토큰이 RefreshToken인 지 검증
        String category = jwtUtil.getCategory(token);
        if (!category.equals("refresh")) {
            throw new ExpectedException(ErrorCode.INVALID_REFRESH_TOKEN_TYPE);
        }

        // RefreshToken이 만료되었는 지 검증
        if (jwtUtil.isExpired(token)) {
            throw new ExpectedException(ErrorCode.REFRESH_TOKEN_IS_EXPIRED);
        }

        // DB에 저장되어 있는지 확인
        if (!refreshTokenRepository.existsByRefreshToken(token)) {
            throw new ExpectedException(ErrorCode.REFRESH_TOKEN_NOT_EXISTS_DATABASE);
        }

        String email = jwtUtil.getEmail(token);
        String role = jwtUtil.getRole(token);

        String newAccessToken = jwtUtil.createJwt("access", email, role, AuthConstants.ACCESS_TOKEN_EXPIRATION_TIME);
        String newRefreshToken = jwtUtil.createJwt("refresh", email, role, AuthConstants.REFRESH_TOKEN_EXPIRATION_TIME);

        // DB에 존재하는 RefreshToken 삭제 후 새 RefreshToken 저장
        refreshTokenRepository.deleteByRefreshToken(token);
        saveRefreshToken(email, newRefreshToken, AuthConstants.REFRESH_TOKEN_EXPIRATION_TIME);

        return new TokenDTO(newAccessToken, newRefreshToken);
    }

    public String validateToken(String accessToken) {
        // AccessToken 헤더의 값이 유효한 지 검증
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            return null;
        }

        String token = accessToken.split(" ")[1];

        // 토큰이 AccessToken인 지 검증
        String category = jwtUtil.getCategory(token);
        if (!category.equals("access")) {
            return null;
        }

        // AccessToken이 만료되었는 지 검증
        if (jwtUtil.isExpired(token)) {
            return null;
        }

        String email = jwtUtil.getEmail(token);
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));
        return foundUser.getNickname();
    }


    private void saveRefreshToken(String email, String refresh, Long expirationTime) {
        Date date = new Date(System.currentTimeMillis() + expirationTime);
        RefreshToken refreshToken = new RefreshToken(email, refresh, date.toString());
        refreshTokenRepository.save(refreshToken);
    }
}
