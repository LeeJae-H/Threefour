package com.threefour.auth.ui;

import com.threefour.auth.ReissueService;
import com.threefour.auth.TokenDTO;
import com.threefour.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;

    /**
     * AccessToken 재발급(By RefreshToken) API
     */
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> reissue(@RequestHeader("RefreshToken") String refreshToken, HttpServletResponse response) {
        TokenDTO tokenDTO = reissueService.reissue(refreshToken);
        response.setHeader("AccessToken", "Bearer " + tokenDTO.getAccessToken());
        response.setHeader("RefreshToken", "Bearer " + tokenDTO.getRefreshToken());
        return ApiResponse.success("ok");
    }
}
