package com.threefour.config;

import com.threefour.auth.JwtUtil;
import com.threefour.auth.AuthConstants;
import com.threefour.auth.RefreshTokenRepository;
import com.threefour.auth.exception.CustomAuthenticationEntryPoint;
import com.threefour.auth.filter.CustomLogoutFilter;
import com.threefour.auth.filter.JwtFilter;
import com.threefour.auth.filter.CustomLoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf((auth) -> auth.disable())             // CsrfFilter 비활성화
                .logout((auth) -> auth.disable())           // LogoutFilter 비활성화
                .formLogin((auth) -> auth.disable())        // UsernamePasswordAuthenticationFilter 비활성화
                .httpBasic((auth) -> auth.disable())        // BasicAuthenticationFilter 비활성화
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  //세션 설정

                // 경로(URL)별 인증/인가 작업 수행 여부
                    // 현재 권한(role)에 따른 인가 작업이 없습니다.
                    // 즉, 인증된 사용자는 모든 인가 작업에 성공합니다.
                .authorizeHttpRequests((auth) -> auth       // -> AuthorizationFilter(= FilterSecurityInterceptor) 활성화
                        .requestMatchers(AuthConstants.WHITELIST_URLS).permitAll()
                        .anyRequest().authenticated())

                // 인증 예외 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))

                // 필터 추가
                .addFilterAt(new CustomLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class)
                .addFilterAt(new CustomLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(jwtUtil), CustomLoginFilter.class)

                .build();
    }

    /**
     * 정적 자원(/css, /images, /js 등)에 대해서 인가 작업을 수행하지 않도록 합니다.
     * permitAll()의 경우와 달리 필터를 거치지 않습니다.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    /**
     * CORS 설정입니다.
     * 서버로 들어오는 요청에 대해 다룹니다.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. 허용 출처 (host + port)
        config.addAllowedOrigin("http://localhost:8080");

        // 2. 허용 HTTP Method
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"));

        // 3. 허용 HTTP Header
        config.setAllowedHeaders(List.of("AccessToken", "RefreshToken"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}