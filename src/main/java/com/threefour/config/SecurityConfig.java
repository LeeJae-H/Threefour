package com.threefour.config;

import com.threefour.auth.JwtUtil;
import com.threefour.auth.AuthConstants;
import com.threefour.auth.RefreshTokenRepository;
import com.threefour.auth.filter.CustomLogoutFilter;
import com.threefour.auth.filter.JwtFilter;
import com.threefour.auth.filter.CustomLoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

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
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  //세션 설정

                // 경로별 인가 작업
                .authorizeHttpRequests((auth) -> auth       // -> Authorization 필터 활성화
                        .requestMatchers(AuthConstants.WHITELIST_URLS).permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated())

                // 필터 추가
                .addFilterAt(new CustomLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class)
                .addFilterAt(new CustomLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(jwtUtil), CustomLoginFilter.class)

                .build();
    }
}
