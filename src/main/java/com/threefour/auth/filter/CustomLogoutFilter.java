package com.threefour.auth.filter;

import com.threefour.auth.JwtUtil;
import com.threefour.auth.RefreshTokenRepository;
import com.threefour.common.ErrorCode;
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

/**
 * 로그아웃 작업을 수행합니다.
 *
 * --Request--
 * POST /logout
 * Header : RefreshToken
 */
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    /**
     * 로그아웃 작업을 수행하는 메소드입니다.
     *
     * 데이터베이스에 존재하는 RefreshToken을 삭제하는데,
     * 다중 로그인 상태일 경우 모두 로그아웃 처리하기 위해
     * 사용자의 email을 기반으로 해당 사용자의 모든 RefreshToken을 삭제합니다.
     */
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!isLogoutRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = request.getHeader("RefreshToken");

        // 1. RefreshToken 헤더의 값이 올바른 형태인지 검증
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            ErrorCode errorCode = ErrorCode.NOT_REFRESH_TOKEN;
            response.setStatus(errorCode.getHttpStatus().value());
            return;
        }

        String token = refreshToken.split(" ")[1];

        // 2. 토큰이 RefreshToken인 지 검증
        String category = jwtUtil.getCategory(token);
        if (!category.equals("refresh")) {
            ErrorCode errorCode = ErrorCode.NOT_REFRESH_TOKEN;
            response.setStatus(errorCode.getHttpStatus().value());
            return;
        }

        // 3. RefreshToken이 만료되었는 지 검증 -> 데이터베이스에 저장되어 있는지 여부로 확인
        if (!refreshTokenRepository.existsByRefreshToken(token)) {
            ErrorCode errorCode = ErrorCode.EXPIRED_REFRESH_TOKEN;
            response.setStatus(errorCode.getHttpStatus().value());
            return;
        }

        String userEmail = jwtUtil.getEmail(token);

        // 데이터베이스에 존재하는 해당 사용자의 모든 RefreshToken 삭제
        refreshTokenRepository.deleteByUserEmail(userEmail);

        // 클라이언트(axios)에서는 2xx 응답만을 성공으로 처리하기 때문에 302가 아닌 200을 응답으로 설정
        response.setStatus(HttpStatus.OK.value());
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();
        return "/logout".equals(requestUri) && "POST".equals(requestMethod);
    }
}
