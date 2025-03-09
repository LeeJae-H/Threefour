package com.threefour.user.ui;

import com.threefour.user.application.UserAccountService;
import com.threefour.user.dto.JoinRequest;
import lombok.RequiredArgsConstructor;
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
     * @param joinRequest (email, password, nickname)
     * @return 사용자 닉네임 (nickname)
     */
    @PostMapping("/join")
    public String join(@RequestBody JoinRequest joinRequest) {
        return userAccountService.join(joinRequest);
    }

    /**
     * 회원탈퇴 API
     *
     * @param refreshToken
     */
    @DeleteMapping
    public String delete(@RequestHeader("RefreshToken") String refreshToken) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userAccountService.deleteUser(refreshToken, email);
        return "ok";
    }
}