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
     * 본인만 가능합니다.
     *
     * @param userId
     * @return MyUserInfoResponse
     */
    @GetMapping("/users/my/{userId}")
    public ResponseEntity<ApiResponse<MyUserInfoResponse>> getMyUserInfo(@PathVariable Long userId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        MyUserInfoResponse myUserInfo = userAccountService.getMyUserInfo(userId, email);
        return ApiResponse.success(myUserInfo);
    }

    /**
     * 내 정보 수정 API
     *
     * 본인만 가능합니다.
     *
     * @param userId
     * @param updateUserInfoRequest
     */
    @PutMapping("/users/my/{userId}")
    public ResponseEntity<ApiResponse<String>> updateMyUserInfo(
            @PathVariable Long userId,
            @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userAccountService.updateMyUserInfo(userId, updateUserInfoRequest, email);
        return ApiResponse.success("ok");
    }

    /**
     * 회원탈퇴 API
     *
     * 본인만 가능합니다.
     *
     * @param userId
     * @param refreshToken
     */
    @DeleteMapping("/users/my/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable Long userId,
            @RequestHeader("RefreshToken") String refreshToken) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userAccountService.deleteUser(userId, refreshToken, email);
        return ApiResponse.success("ok");
    }
}