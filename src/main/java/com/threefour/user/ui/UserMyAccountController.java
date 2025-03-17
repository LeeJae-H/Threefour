package com.threefour.user.ui;

import com.threefour.common.ApiResponse;
import com.threefour.user.application.UserMyAccountService;
import com.threefour.user.dto.MyInfoResponse;
import com.threefour.user.dto.UpdateMyInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/my")
public class UserMyAccountController {

    private final UserMyAccountService userMyAccountService;

    /**
     * 내 정보 조회 API
     *
     * @return 내 정보 (email, nickname)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<MyInfoResponse>> getMyInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        MyInfoResponse myInfo = userMyAccountService.getMyInfo(email);
        return ApiResponse.success(myInfo);
    }

    /**
     * 내 정보 수정 API
     *
     * @param updateMyInfoRequest
     */
    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateMyInfo(@RequestBody UpdateMyInfoRequest updateMyInfoRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userMyAccountService.updateMyInfo(updateMyInfoRequest, email);
        return ApiResponse.success("success");
    }

    /**
     * 회원탈퇴 API
     *
     * @param refreshToken
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteUser(@RequestHeader("RefreshToken") String refreshToken) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userMyAccountService.deleteUser(refreshToken, email);
        return ApiResponse.success("success");
    }
}