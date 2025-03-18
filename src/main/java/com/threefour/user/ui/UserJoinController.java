package com.threefour.user.ui;

import com.threefour.common.ApiResponse;
import com.threefour.user.application.UserJoinService;
import com.threefour.user.dto.EmailAuthNumberRequest;
import com.threefour.user.dto.JoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/join")
public class UserJoinController {

    private final UserJoinService userJoinService;

    /**
     * 회원가입 API
     *
     * @param joinRequest
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> join(@RequestBody JoinRequest joinRequest) {
        userJoinService.join(joinRequest);
        return ApiResponse.success("success");
    }

    /**
     * 이메일 인증번호 발송 API
     *
     * @param emailMap
     */
    @PostMapping("/send-email")
    public ResponseEntity<ApiResponse<String>> sendEmailAuthNumber(@RequestBody Map<String, String> emailMap) {
        userJoinService.sendEmailAuthNumber(emailMap.get("email"));
        return ApiResponse.success("success");
    }

    /**
     * 이메일 인증번호 확인 API
     *
     * @param emailAuthNumberRequest
     */
    @PostMapping("/validate-email")
    public ResponseEntity<ApiResponse<String>> validateEmailAuthNumber(@RequestBody EmailAuthNumberRequest emailAuthNumberRequest) {
        userJoinService.validateEmailAuthNumber(emailAuthNumberRequest);
        return ApiResponse.success("success");
    }

    /**
     * 닉네임 사용 가능 여부 확인 API
     *
     * @param nickname
     */
    @GetMapping("/validate-nickname")
    public ResponseEntity<ApiResponse<String>> validateNickname(@RequestParam String nickname) {
        userJoinService.validateNickname(nickname);
        return ApiResponse.success("success");
    }
}
