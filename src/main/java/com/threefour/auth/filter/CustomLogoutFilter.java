package com.threefour.auth.filter;

import com.threefour.auth.JwtUtil;
import com.threefour.auth.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    /**
     * 로그아웃 요청에 대한 작업을 수행하는 메소드입니다.
     *
     * 데이터베이스에 존재하는 RefreshToken을 삭제하는데,
     * 다중 로그인 상태일 경우 모두 로그아웃 처리하기 위해
     * 사용자의 email을 기반으로 해당 사용자의 모든 RefreshToken을 삭제합니다.
     * RefreshToken이 데이터베이스에 존재하지 않거나 만료된 경우,
     * 로그아웃 성공으로 간주하여 성공 응답을 전달합니다.
     */
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 요청의 uri와 method를 확인하여 로그아웃 요청인 지 검증
        if (!isLogoutRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = request.getHeader("RefreshToken");

        // RefreshToken 헤더의 값이 유효한 지 검증
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setHeader("Location", "/home");
            return;
        }

        String token = refreshToken.split(" ")[1];

        // 토큰이 RefreshToken인 지 검증
        String category = jwtUtil.getCategory(token);
        if (!category.equals("refresh")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setHeader("Location", "/home");
            return;
        }

        // DB에 저장되어 있는지 확인
        if (!refreshTokenRepository.existsByRefreshToken(token)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setHeader("Location", "/home");
            return;
        }

        String userEmail = jwtUtil.getEmail(token);

        // DB에 존재하는 해당 사용자의 모든 RefreshToken 삭제
        refreshTokenRepository.deleteByUserEmail(userEmail);

        // 클라이언트(axios)에서는 2xx 응답만을 성공으로 처리하기 때문에 302가 아닌 200을 응답으로 설정합니다.
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Location", "/home");
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();
        return "/logout".equals(requestUri) && "POST".equals(requestMethod);
    }
}
