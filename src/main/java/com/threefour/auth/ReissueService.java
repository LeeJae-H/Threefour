package com.threefour.auth;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JwtUtil jwtUtil;

    public TokenDTO reissue(String refreshToken) {
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

        String email = jwtUtil.getEmail(token);
        String role = jwtUtil.getRole(token);

        String newAccessToken = jwtUtil.createJwt("access", email, role, AuthConstants.ACCESS_TOKEN_EXPIRATION_TIME);
        String newRefreshToken = jwtUtil.createJwt("refresh", email, role, AuthConstants.REFRESH_TOKEN_EXPIRATION_TIME);

        return new TokenDTO(newAccessToken, newRefreshToken);
    }
}
