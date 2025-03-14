package com.threefour.user.ui;

import com.threefour.common.ApiResponse;
import com.threefour.user.application.UserAccountService;
import com.threefour.user.dto.MyUserInfoResponse;
import com.threefour.user.dto.UpdateUserInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserMyAccountController {

    private final UserAccountService userAccountService;

    /**
     * 내 정보 조회 API
     *
     * @return MyUserInfoResponse
     */
    @GetMapping("/users/my/info")
    public ResponseEntity<ApiResponse<MyUserInfoResponse>> getMyUserInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        MyUserInfoResponse myUserInfo = userAccountService.getMyUserInfo(email);
        return ApiResponse.success(myUserInfo);
    }

    /**
     * 내 정보 수정 API
     *
     * @param updateUserInfoRequest
     */
    @PutMapping("/users/my/info")
    public ResponseEntity<ApiResponse<String>> updateMyUserInfo(@RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userAccountService.updateMyUserInfo(updateUserInfoRequest, email);
        return ApiResponse.success("ok");
    }

    /**
     * 회원탈퇴 API
     *
     * @param refreshToken
     */
    @DeleteMapping("/users/my/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@RequestHeader("RefreshToken") String refreshToken) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userAccountService.deleteUser(refreshToken, email);
        return ApiResponse.success("ok");
    }
}