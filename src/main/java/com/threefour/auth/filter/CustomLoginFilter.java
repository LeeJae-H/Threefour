package com.threefour.auth.filter;

import com.threefour.auth.*;
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

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * 로그인 작업을 수행합니다.
 *
 * --Request--
 * POST /login
 * Content-Type : application/x-www-form-urlencoded
 * Parameter : email, password
 */
@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 로그인 작업을 수행하는 메서드입니다.
     *
     * 실제로 로그인 작업을 수행하는 객체는 AuthenticationManager이며,
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

     * 모바일, PC 등 다중 로그인을 진행할 경우를 고려하여,
     * 로그인 시 데이터베이스에 존재하는 RefreshToken은 삭제하지 않습니다.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        // 사용자 정보(email, role) 추출
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String email = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 토큰 생성
        String accessToken = jwtUtil.createJwt("access", email, role, AuthConstants.ACCESS_TOKEN_EXPIRATION_TIME);
        String refreshToken = jwtUtil.createJwt("refresh", email, role, AuthConstants.REFRESH_TOKEN_EXPIRATION_TIME);

        // 데이터베이스에 RefreshToken 저장
        saveRefreshToken(email, refreshToken, AuthConstants.REFRESH_TOKEN_EXPIRATION_TIME);

        // Response Header에 AccessToken과 RefreshToken을 추가
        response.setHeader("AccessToken", "Bearer " + accessToken);
        response.setHeader("RefreshToken", "Bearer " + refreshToken);

        // 클라이언트(axios)에서는 2xx 응답만을 성공으로 처리하기 때문에 302가 아닌 200을 응답으로 설정
        response.setStatus(HttpStatus.OK.value());
    }

    /**
     * 로그인 실패 시 실행하는 메소드입니다.
     *
     * 모든 경우(ex) 존재하지 않는 이메일, 아이디나 비밀번호가 입력되지 않음 등)에 401 상태코드를 응답합니다.
     * todo 아이디나 비밀번호가 입력되지 않은 경우는 400 상태코드 처리
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }

    private void saveRefreshToken(String email, String refresh, Long expirationTime) {
        Date date = new Date(System.currentTimeMillis() + expirationTime);
        RefreshToken refreshToken = new RefreshToken(email, refresh, date.toString());
        refreshTokenRepository.save(refreshToken);
    }
}