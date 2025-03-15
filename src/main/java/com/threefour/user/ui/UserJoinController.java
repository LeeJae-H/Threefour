package com.threefour.user.ui;

import com.threefour.common.ApiResponse;
import com.threefour.user.application.UserJoinService;
import com.threefour.user.dto.EmailValidationRequest;
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
     * @return 사용자 닉네임
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> join(@RequestBody JoinRequest joinRequest) {
        String nickname = userJoinService.join(joinRequest);
        return ApiResponse.success(nickname);
    }

    /**
     * 회원가입 시 사용 - 이메일 인증번호 발송 API
     *
     * @param emailMap
     */
    @PostMapping("/send-email")
    public ResponseEntity<ApiResponse<String>> sendEmailAuthNumberForJoin(@RequestBody Map<String, String> emailMap) {
        userJoinService.sendEmailAuthNumberForJoin(emailMap.get("email"));
        return ApiResponse.success("success");
    }

    /**
     * 회원가입 시 사용 - 이메일 인증번호 확인 API
     *
     * @param emailValidationRequest
     */
    @PostMapping("/validate-email")
    public ResponseEntity<ApiResponse<String>> validateEmailForJoin(@RequestBody EmailValidationRequest emailValidationRequest) {
        userJoinService.validateEmailForJoin(emailValidationRequest);
        return ApiResponse.success("success");
    }

    /**
     * 회원가입 시 사용 - 닉네임 사용 가능 여부 확인 API
     *
     * @param nickname
     */
    @GetMapping("/validate-nickname")
    public ResponseEntity<ApiResponse<String>> validateNicknameForJoin(@RequestParam String nickname) {
        userJoinService.validateNicknameForJoin(nickname);
        return ApiResponse.success("success");
    }
}
