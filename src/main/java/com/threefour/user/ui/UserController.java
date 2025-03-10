package com.threefour.user.ui;

import com.threefour.common.ApiResponse;
import com.threefour.user.application.UserService;
import com.threefour.user.dto.MyUserInfoResponse;
import com.threefour.user.dto.OtherUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 조회 API
     *
     * 본인만 가능합니다.
     *
     * @param userId
     * @return MyUserInfoResponse
     */
    @GetMapping("/my/{userId}")
    public ResponseEntity<ApiResponse<MyUserInfoResponse>> getMyUserInfo(@PathVariable Long userId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        MyUserInfoResponse myUserInfo = userService.getMyUserInfo(userId, email);
        return ApiResponse.success(myUserInfo);
    }

    /**
     * 다른 회원 정보 조회 API
     *
     * 조회한 회원이 본인이라도, 다른 회원으로 취급합니다.
     *
     * @param userId
     * @return OtherUserInfoResponse
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<OtherUserInfoResponse>> getOtherUserInfo(@PathVariable Long userId) {
        OtherUserInfoResponse otherUserInfo = userService.getOtherUserInfo(userId);
        return ApiResponse.success(otherUserInfo);
    }
}
