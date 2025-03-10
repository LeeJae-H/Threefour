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
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateUserInfo(@RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userAccountService.updateUserInfo(updateUserInfoRequest, email);
        return ApiResponse.success("ok");
    }

    /**
     * 회원탈퇴 API
     *
     * @param refreshToken
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteUser(@RequestHeader("RefreshToken") String refreshToken) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userAccountService.deleteUser(refreshToken, email);
        return ApiResponse.success("ok");
    }
}