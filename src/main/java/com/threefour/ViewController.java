package com.threefour;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    /**
     * 홈 화면 API
     */
    @GetMapping("/home")
    public String getHomePage() {
        return "home";
    }

    /**
     * 회원가입 화면 API
     */
    @GetMapping("/users/join")
    public String getJoinPage() {
        return "join";
    }
}