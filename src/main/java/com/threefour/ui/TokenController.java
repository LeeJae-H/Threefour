package com.threefour.ui;

import com.threefour.application.TokenService;
import com.threefour.dto.auth.TokenResponse;
import com.threefour.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {

    private final TokenService tokenService;

    /**
     * AccessToken, RefreshToken 재발급 API
     * Refresh Token Rotation 방식입니다.
     *
     * @param refreshToken
     */
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> reissueToken(
            @RequestHeader("RefreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = tokenService.reissueToken(refreshToken);
        response.setHeader("AccessToken", "Bearer " + tokenResponse.getAccessToken());
        response.setHeader("RefreshToken", "Bearer " + tokenResponse.getRefreshToken());
        return new ResponseEntity<>(ApiResponse.success("success"), HttpStatus.OK);
    }

    /**
     * AccessToken 검증 API
     *
     * 뷰(html)를 리턴하는 API는 인증/인가를 거치지 않기 때문에,
     * 인증/인가가 필요한 뷰(ex) 게시글 작성)를 로드하는 시점에
     * 클라이언트(js)에서 호출하는 API입니다.
     *
     * @return 사용자 닉네임
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateAccessToken() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String nickname = tokenService.getUserNickname(email);
        return new ResponseEntity<>(ApiResponse.success(nickname), HttpStatus.OK);
    }
}
