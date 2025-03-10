package com.threefour.user.application;

import com.threefour.auth.JwtUtil;
import com.threefour.auth.RefreshTokenRepository;
import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.post.domain.PostRepository;
import com.threefour.user.domain.EncodePasswordService;
import com.threefour.user.domain.User;
import com.threefour.user.domain.UserRepository;
import com.threefour.user.dto.JoinRequest;
import com.threefour.user.dto.UpdateUserInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserRepository userRepository;
    private final EncodePasswordService encodePasswordService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PostRepository postRepository;

    @Transactional
    public String join(JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        String password = joinRequest.getPassword();
        String nickname = joinRequest.getNickname();

        if (userRepository.existsByEmail(email)) {
            throw new ExpectedException(ErrorCode.ALREADY_EXIST_USER);
        }
        validatePassword(password);
        validateNickname(nickname);

        User newUser = User.join(email, encodePasswordService.encode(password), nickname);
        userRepository.save(newUser);
        return newUser.getNickname();
    }

    @Transactional
    public void updateUserInfo(UpdateUserInfoRequest updateUserInfoRequest, String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));

        // 회원 정보의 변경이 이루어졌는지 여부
        boolean isUpdated = false;

        if (updateUserInfoRequest.getPassword() != null) {
            String newPassword = updateUserInfoRequest.getPassword();
            validatePassword(newPassword);
            foundUser.changePassword(encodePasswordService.encode(newPassword));
            isUpdated = true;
        }

        if (updateUserInfoRequest.getNickname() != null) {
            String newNickname = updateUserInfoRequest.getNickname();
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

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new ExpectedException(ErrorCode.INVALID_PASSWORD_LENGTH);
        }
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.length() < 2 || nickname.length() > 10) {
            throw new ExpectedException(ErrorCode.INVALID_NICKNAME_LENGTH);
        }
        if (!nickname.matches("^[a-zA-Z0-9가-힣]+$")) {
            throw new ExpectedException(ErrorCode.INVALID_NICKNAME_FORMAT);
        }
    }
}