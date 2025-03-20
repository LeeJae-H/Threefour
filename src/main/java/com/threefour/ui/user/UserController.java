package com.threefour.ui.user;

import com.threefour.common.ApiResponse;
import com.threefour.application.user.UserService;
import com.threefour.dto.user.OtherUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Deprecated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
        return new ResponseEntity<>(ApiResponse.success(otherUserInfo), HttpStatus.OK);
    }
}
