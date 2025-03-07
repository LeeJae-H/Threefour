package com.threefour.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    /**
     * 인증(Authentication)을 수행하는 메서드입니다.
     *
     * 실제로 인증(Authentication)을 수행하는 객체는 AuthenticationManager이며,
     * AuthenticationManager는 UserDetailsService를 사용합니다.
     * UserDetailsService는 데이터베이스에서 사용자 정보를 가져오는 역할을 하며,
     * loadUserByUsername() 메서드는 UserDetails 타입을 리턴합니다.
     *
     * CustomUserDetailsService에서 UserDetailsService를 구현했습니다.
     * CustomUserDetails에서 UserDetails를 구현했습니다.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        System.out.println("-----------success-------------");
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        System.out.println("-----------fail-------------");
    }

    /**
     * Spring Security가 기본적으로 사용하는 username의 필드명을 email로 변경하는 메서드입니다.
     *
     * 추가적으로, 아래의 작업도 함께 해야 합니다.
     * CustomUserDetailsService의 loadUserByUsername() 메서드에서 email로 데이터베이스에서 사용자 정보를 가져옵니다.
     * CustomUserDetails의 getUsername() 메서드에서 사용자의 email을 가져옵니다.
     */
//    @Override
//    public void setUsernameParameter(String usernameParameter) {
//        super.setUsernameParameter("email");
//    }
}