package com.threefour.infrastructure.auth;

import com.threefour.domain.user.User;
import com.threefour.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자 정보를 데이터베이스에서 가져오는 메서드로, AuthenticationManager가 사용합니다.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Spring Security가 기본적으로 사용하는 username의 필드를 email 필드로 변경
        Optional<User> foundUser = userRepository.findByEmail(email);

        if (foundUser.isPresent()) {
            return new CustomUserDetails(foundUser.get());
        }
        return null;
    }
}
