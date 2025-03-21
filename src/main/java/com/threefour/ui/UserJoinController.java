package com.threefour.ui;

import com.threefour.common.ApiResponse;
import com.threefour.application.UserJoinService;
import com.threefour.dto.user.EmailAuthNumberRequest;
import com.threefour.dto.user.JoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        String nickname = userJoinService.join(joinRequest);
        return new ResponseEntity<>(ApiResponse.success(nickname), HttpStatus.OK);
    }

    /**
     * 이메일 인증번호 발송 API
     *
     * @param emailMap
     */
    @PostMapping("/send-email")
    public ResponseEntity<ApiResponse<String>> sendEmailAuthNumber(@RequestBody Map<String, String> emailMap) {
        userJoinService.sendEmailAuthNumber(emailMap.get("email"));
        return new ResponseEntity<>(ApiResponse.success("success"), HttpStatus.OK);
    }

    /**
     * 이메일 인증번호 확인 API
     *
     * @param emailAuthNumberRequest
     */
    @PostMapping("/validate-email")
    public ResponseEntity<ApiResponse<String>> validateEmailAuthNumber(@RequestBody EmailAuthNumberRequest emailAuthNumberRequest) {
        userJoinService.validateEmailAuthNumber(emailAuthNumberRequest);
        return new ResponseEntity<>(ApiResponse.success("success"), HttpStatus.OK);
    }

    /**
     * 닉네임 사용 가능 여부 확인 API
     *
     * @param nickname
     */
    @GetMapping("/validate-nickname")
    public ResponseEntity<ApiResponse<String>> validateNickname(@RequestParam String nickname) {
        userJoinService.validateNickname(nickname);
        return new ResponseEntity<>(ApiResponse.success("success"), HttpStatus.OK);
    }
}
