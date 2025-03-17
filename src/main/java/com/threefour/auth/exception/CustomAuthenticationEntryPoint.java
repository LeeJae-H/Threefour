package com.threefour.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.threefour.common.ApiResponse;
import com.threefour.common.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * 인증에 실패했을 때(in JwtFilter) 응답을 생성하는 역할을 합니다.
 */
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Response Body 작성 (ApiResponse)
            String code = errorCode.getCode();
            String message = errorCode.getMessage();
            String json = new ObjectMapper().writeValueAsString(ApiResponse.error(code, message));
            response.getWriter().write(json);

            String clientIp = getClientIp(request);
            String requestUri = request.getRequestURI();
            log.error("인증되지 않은 사용자 {} 로부터 {} 로의 접근", clientIp, requestUri);
        } catch (Exception ex) {
            log.error("인증 예외 응답 생성 과정에서 오류 발생 :", ex);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");

        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        } else {
            clientIp = clientIp.split(",")[0];
        }

        return clientIp;
    }
}
