package com.threefour.auth.ui;

import com.threefour.auth.TokenService;
import com.threefour.auth.TokenDTO;
import com.threefour.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
     * AccessToken, RefreshToken 재발급(By RefreshToken) API
     *
     * @param refreshToken
     */
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> reissue(@RequestHeader("RefreshToken") String refreshToken, HttpServletResponse response) {
        TokenDTO tokenDTO = tokenService.reissueToken(refreshToken);
        response.setHeader("AccessToken", "Bearer " + tokenDTO.getAccessToken());
        response.setHeader("RefreshToken", "Bearer " + tokenDTO.getRefreshToken());
        return ApiResponse.success("success");
    }

    /**
     * AccessToken 유효성 검증 API
     *
     * @param accessToken
     * @return 성공 시 200 응답과 사용자 닉네임 / 실패 시 400 응답
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestHeader("AccessToken") String accessToken) {
        String nickname = tokenService.validateToken(accessToken);
        return ApiResponse.success(nickname);
    }
}
