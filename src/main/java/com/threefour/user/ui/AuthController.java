package com.threefour.user.ui;

import com.threefour.user.application.AuthService;
import com.threefour.user.dto.JoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 API
     *
     * @param joinRequest (email, password, nickname)
     * @return 사용자 닉네임 (nickname)
     */
    @PostMapping("/join")
    public String join(@RequestBody JoinRequest joinRequest) {
        return authService.join(joinRequest);
    }

    /**
     * 회원탈퇴 API
     */
//    @DeleteMapping
//    public String delete(@RequestHeader("Authorization") String token) {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        authService.deleteUser(email);
//        return "ok";
//    }
}