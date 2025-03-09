package com.threefour.auth.filter;

import com.threefour.auth.AuthConstants;
import com.threefour.auth.CustomUserDetails;
import com.threefour.auth.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

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

    /**
     * 로그인 성공 시 실행하는 메소드입니다.
     *
     * 사용자의 email, role로 AccessToken, RefreshToken을 발급합니다.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String email = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtUtil.createJwt("access", email, role, AuthConstants.ACCESS_TOKEN_EXPIRATION_TIME);
        String refreshToken = jwtUtil.createJwt("refresh", email, role, AuthConstants.REFRESH_TOKEN_EXPIRATION_TIME);

        response.setHeader("AccessToken", "Bearer " + accessToken);
        response.setHeader("RefreshToken", "Bearer " + refreshToken);
        response.setStatus(HttpStatus.OK.value());
    }

    /**
     * 로그인 실패 시 실행하는 메소드입니다.
     *
     * 401 응답 코드를 반환합니다.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
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