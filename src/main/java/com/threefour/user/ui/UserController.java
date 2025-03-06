package com.threefour.user.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/test/no-auth")
    public String testNoAuth() {
        return "success";
    }

    @GetMapping("/test/auth")
    public String testAuth() {
        return "success";
    }
}
