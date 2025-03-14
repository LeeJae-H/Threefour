package com.threefour.user.ui;

import com.threefour.common.ApiResponse;
import com.threefour.user.application.UserJoinService;
import com.threefour.user.dto.EmailValidationRequest;
import com.threefour.user.dto.JoinRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserJoinController {

    private final UserJoinService userJoinService;

    /**
     * 회원가입 API
     *
     * @param joinRequest
     * @return 사용자 닉네임 (nickname)
     */
    @PostMapping("/api/users/join")
    public ResponseEntity<ApiResponse<String>> join(@RequestBody JoinRequest joinRequest, HttpServletResponse response) {
        String nickname = userJoinService.join(joinRequest);
        response.setHeader("Location", "/home");
        return ApiResponse.success(nickname);
    }

    /**
     * 회원가입 시 사용 - 이메일 인증번호 발송 API
     *
     * @param email
     * @return 성공 시 200 응답 / 실패 시 예외 응답
     */
    @PostMapping("/api/users/send-email")
    public ResponseEntity<ApiResponse<String>> sendEmailAuthNumberForJoin(@RequestParam String email) {
        userJoinService.sendEmailAuthNumberForJoin(email);
        return ApiResponse.success("success");
    }

    /**
     * 회원가입 시 사용 - 이메일 인증번호 확인 API
     *
     * @param emailValidationRequest
     * @return 성공 시 200 응답 / 실패 시 예외 응답
     */
    @PostMapping("/api/users/validate-email")
    public ResponseEntity<ApiResponse<String>> validateEmailForJoin(@RequestBody EmailValidationRequest emailValidationRequest) {
        userJoinService.validateEmailForJoin(emailValidationRequest);
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
        userJoinService.validateNicknameForJoin(nickname);
        return ApiResponse.success("success");
    }
}
