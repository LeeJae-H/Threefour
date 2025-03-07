package com.threefour.user.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @GetMapping("/admin")
    public String testAuth() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return email;
    }

    @GetMapping("/testlog")
    public String testLog() {
      log.info("hi");
      return "hi";
    }
}
