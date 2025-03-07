package com.threefour.user.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.user.domain.User;
import com.threefour.user.domain.UserRepository;
import com.threefour.user.dto.request.JoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public String join(JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        String password = bCryptPasswordEncoder.encode(joinRequest.getPassword());
        String name = joinRequest.getName();

        if (userRepository.existsByEmail(email)) {
            throw new ExpectedException(ErrorCode.ALREADY_EXIST_USER);
        }

        User newUser = new User(email, password, name, "ROLE_USER");
        User foundUser = userRepository.save(newUser);
        return foundUser.getName();
    }
}
