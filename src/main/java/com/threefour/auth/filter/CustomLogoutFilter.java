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

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 로그아웃 요청인 지 검증
        if (!isLogoutRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = request.getHeader("RefreshToken");

        // RefreshToken 헤더의 값이 유효한 지 검증
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            // todo 예외 발생 로직 추가
            return;
        }

        String token = refreshToken.split(" ")[1];

        // 토큰이 RefreshToken인 지 검증
        String category = jwtUtil.getCategory(token);
        if (!category.equals("refresh")) {
            // todo 예외 발생 로직 추가
            return;
        }

        // RefreshToken이 만료되었는 지 검증
        if (jwtUtil.isExpired(token)) {
            // todo 예외 발생 로직 추가
            return;
        }

        // DB에 저장되어 있는지 확인
        if (!refreshTokenRepository.existsByRefreshToken(token)) {
            // todo 예외 발생 로직 추가
            return;
        }

        // DB에 기존의 RefreshToken 삭제
        refreshTokenRepository.deleteByRefreshToken(token);

        response.setStatus(HttpStatus.OK.value());
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();
        return requestUri.matches("^\\/logout$") && requestMethod.equals("POST");
    }
}
