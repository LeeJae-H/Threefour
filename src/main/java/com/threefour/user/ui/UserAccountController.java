package com.threefour.user.ui;

import com.threefour.common.ApiResponse;
import com.threefour.user.application.UserAccountService;
import com.threefour.user.dto.JoinRequest;
import com.threefour.user.dto.UpdateUserInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    /**
     * 회원가입 API
     *
     * @param joinRequest
     * @return 사용자 닉네임 (nickname)
     */
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<String>> join(@RequestBody JoinRequest joinRequest) {
        String nickname = userAccountService.join(joinRequest);
        return ApiResponse.success(nickname);
    }

    /**
     * 회원 정보 수정 API
     *
     * @param updateUserInfoRequest
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> updateUserInfo(
            @PathVariable Long userId,
            @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userAccountService.updateUserInfo(userId, updateUserInfoRequest, email);
        return ApiResponse.success("ok");
    }

    /**
     * 회원탈퇴 API
     *
     * @param refreshToken
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable Long userId,
            @RequestHeader("RefreshToken") String refreshToken) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userAccountService.deleteUser(userId, refreshToken, email);
        return ApiResponse.success("ok");
    }
}