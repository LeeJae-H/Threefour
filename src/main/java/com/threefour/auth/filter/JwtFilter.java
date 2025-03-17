package com.threefour.auth.filter;

import com.threefour.auth.CustomUserDetails;
import com.threefour.auth.JwtUtil;
import com.threefour.auth.AuthConstants;
import com.threefour.common.ErrorCode;
import com.threefour.user.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 인증 작업을 수행하는 필터입니다.
 * 인증 실패 시 CustomAuthenticationEntryPoint에서 응답을 생성합니다.
 */
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * JwtFilter의 실행 여부를 판단하는 메서드입니다.
     * SecurityConfig의 permitAll()에 해당하는 요청에 대해서 해당 필터 실행을 하지 않도록 하기 위함입니다.
     *
     * @return true(필터 실행 x) / false(필터 실행 o)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PatternMatchUtils.simpleMatch(AuthConstants.WHITELIST_URLS, request.getRequestURI());
    }

    /**
     * 인증 작업을 수행하는 메서드로, AccessToken을 검증합니다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader("AccessToken");

        // 1. AccessToken 헤더의 값이 올바른 형태인지 검증
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            request.setAttribute("exception", ErrorCode.NOT_ACCESS_TOKEN);
            filterChain.doFilter(request, response);
            return;
        }

        String token = accessToken.split(" ")[1];

        // 2. 토큰이 AccessToken인 지 검증
        String category = jwtUtil.getCategory(token);
        if (!category.equals("access")) {
            request.setAttribute("exception", ErrorCode.NOT_ACCESS_TOKEN);
            filterChain.doFilter(request, response);
            return;
        }

        // 3. AccessToken이 만료되었는 지 검증
        if (jwtUtil.isExpired(token)) {
            request.setAttribute("exception", ErrorCode.EXPIRED_ACCESS_TOKEN);
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtUtil.getEmail(token);
        String role = jwtUtil.getRole(token);
        User user = new User(email, "temppassword", role);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 경로별 인증/인가 작업(By AuthorizationFilter)을 위해, SecurityContextHolder(세션)에 사용자 정보를 담음
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
