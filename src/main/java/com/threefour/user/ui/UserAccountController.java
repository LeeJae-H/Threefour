package com.threefour.user.ui;

import com.threefour.common.ApiResponse;
import com.threefour.user.application.UserAccountService;
import com.threefour.user.dto.JoinRequest;
import com.threefour.user.dto.MyUserInfoResponse;
import com.threefour.user.dto.UpdateUserInfoRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    /**
     * 회원가입 API
     *
     * @param joinRequest
     * @return 사용자 닉네임 (nickname)
     */
    @PostMapping("/api/users/join")
    public ResponseEntity<ApiResponse<String>> join(@RequestBody JoinRequest joinRequest, HttpServletResponse response) {
        String nickname = userAccountService.join(joinRequest);
        response.setHeader("Location", "/home");
        return ApiResponse.success(nickname);
    }

    /**
     * 회원가입 시 사용 - 이메일 인증번호 발송 API
     *
     * @param email
     * @return 성공 시 200 응답 / 실패 시 예외 응답
     */
    @GetMapping("/api/users/send-email")
    public ResponseEntity<ApiResponse<String>> sendEmailAuthNumberForJoin(@RequestParam String email) {
        userAccountService.sendEmailAuthNumberForJoin(email);
        return ApiResponse.success("success");
    }

    /**
     * 회원가입 시 사용 - 닉네임 사용 가능 여부 확인 API
     *
     * @param nickname
     * @return 성공 시 200 응답 / 실패 시 예외 응답
     */
    @GetMapping("/api/users/validate-nickname")
    public ResponseEntity<ApiResponse<String>> validateNicknameForJoin(@RequestParam String nickname) {
        userAccountService.validateNicknameForJoin(nickname);
        return ApiResponse.success("success");
    }

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