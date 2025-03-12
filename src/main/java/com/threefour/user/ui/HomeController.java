package com.threefour.user.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * 홈 화면 API
     */
    @GetMapping("/home")
    public String home() {
        return "home";
    }
}