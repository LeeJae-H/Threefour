package com.threefour.user.application;

import com.threefour.auth.JwtUtil;
import com.threefour.auth.RefreshTokenRepository;
import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.post.domain.PostRepository;
import com.threefour.user.domain.User;
import com.threefour.user.domain.UserRepository;
import com.threefour.user.dto.MyInfoResponse;
import com.threefour.user.dto.UpdateMyInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserMyAccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PostRepository postRepository;

    public MyInfoResponse getMyInfo(String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));

        return new MyInfoResponse(foundUser.getEmail(), foundUser.getNickname());
    }

    @Transactional
    public void updateMyInfo(UpdateMyInfoRequest updateMyInfoRequest, String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));

        // 회원 정보 변경이 이루어졌는지 여부
        boolean isUpdated = false;

        if (updateMyInfoRequest.getPassword() != null) {
            String newPassword = updateMyInfoRequest.getPassword();
            validatePassword(newPassword);
            // 비밀번호는 암호화한 후 전달
            foundUser.changePassword(passwordEncoder.encode(newPassword));
            isUpdated = true;
        }

        if (updateMyInfoRequest.getNickname() != null) {
            String newNickname = updateMyInfoRequest.getNickname();
            validateNickname(newNickname);
            foundUser.changeNickname(newNickname);
            isUpdated = true;
        }

        if (isUpdated) {
            foundUser.updateUpdatedAt();
        }
    }

    @Transactional
    public void deleteUser(String refreshToken, String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));

        // 회원이 작성한 게시글 모두 삭제
        postRepository.deleteByAuthorNickname(foundUser.getNickname());

        // DB에서 회원 삭제
        userRepository.delete(foundUser);

        // RefreshToken 헤더의 값이 유효한 지 검증
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new ExpectedException(ErrorCode.INVALID_REFRESH_TOKEN_FORMAT);
        }

        String token = refreshToken.split(" ")[1];

        // 토큰이 RefreshToken인 지 검증
        String category = jwtUtil.getCategory(token);
        if (!category.equals("refresh")) {
            throw new ExpectedException(ErrorCode.INVALID_REFRESH_TOKEN_TYPE);
        }

        // RefreshToken이 만료되었는 지 검증
        if (jwtUtil.isExpired(token)) {
            throw new ExpectedException(ErrorCode.REFRESH_TOKEN_IS_EXPIRED);
        }

        // DB에 저장되어 있는지 확인
        if (!refreshTokenRepository.existsByRefreshToken(token)) {
            throw new ExpectedException(ErrorCode.REFRESH_TOKEN_NOT_EXISTS_DATABASE);
        }

        // DB에 존재하는 해당 사용자의 모든 RefreshToken 삭제
        refreshTokenRepository.deleteByUserEmail(email);
    }

    // 비밀번호는 최소 8자 이상이어야 하며, 공백을 포함할 수 없다.
    private void validatePassword(String password) {
        if (password == null || password.contains(" ") || password.length() < 8) {
            throw new ExpectedException(ErrorCode.INVALID_PASSWORD_LENGTH);
        }
    }

    // 닉네임은 (양쪽 끝의 공백 제거 후) 2~10자 이내여야 하며, 특수문자를 포함할 수 없다. 또한, 닉네임은 고유하다.
    private void validateNickname(String nickname) {
        if (nickname == null) {
            throw new ExpectedException(ErrorCode.INVALID_NICKNAME_LENGTH);
        }
        String trimmedNickname = nickname.trim();
        if (trimmedNickname.length() < 2 || trimmedNickname.length() > 10) {
            throw new ExpectedException(ErrorCode.INVALID_NICKNAME_LENGTH);
        }
        if (!trimmedNickname.matches("^[a-zA-Z0-9가-힣]+$")) {
            throw new ExpectedException(ErrorCode.INVALID_NICKNAME_FORMAT);
        }
        if (userRepository.existsByNickname(trimmedNickname)) {
            throw new ExpectedException(ErrorCode.ALREADY_USED_NICKNAME);
        }
    }
}