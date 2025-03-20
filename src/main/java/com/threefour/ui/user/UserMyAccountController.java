package com.threefour.ui.user;

import com.threefour.common.ApiResponse;
import com.threefour.application.user.UserMyAccountService;
import com.threefour.dto.user.MyInfoResponse;
import com.threefour.dto.user.UpdateMyInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return new ResponseEntity<>(ApiResponse.success(myInfo), HttpStatus.OK);
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
        return new ResponseEntity<>(ApiResponse.success("success"), HttpStatus.OK);
    }

    /**
     * 닉네임 사용 가능 여부 확인 API - 내 정보 수정 시 사용
     *
     * @param nickname
     */
    @GetMapping("/validate-nickname")
    public ResponseEntity<ApiResponse<String>> validateNickname(@RequestParam String nickname) {
        userMyAccountService.validateNickname(nickname);
        return new ResponseEntity<>(ApiResponse.success("success"), HttpStatus.OK);
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
        return new ResponseEntity<>(ApiResponse.success("success"), HttpStatus.OK);
    }
}