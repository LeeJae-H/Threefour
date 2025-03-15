package com.threefour;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 페이지(html)를 불러오는 GET api는 Security Filter를 거치지 않습니다.
 * 해당 html 로드 시점에 AccessToken을 검증하는 API를 호출해서 인증하는 방식입니다
 */
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

    /**
     * 마이페이지 화면 API
     *
     * 회원만 가능합니다.
     */
    @GetMapping("/users/my/info")
    public String getMyInfoPage() {
        return "user/myInfo";
    }
}