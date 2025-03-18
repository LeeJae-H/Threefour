package com.threefour.user.application;

import com.threefour.auth.JwtProvider;
import com.threefour.auth.RefreshTokenRepository;
import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.post.domain.PostRepository;
import com.threefour.user.domain.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public MyInfoResponse getMyInfo(String email) {
        User foundUser = getUserByEmail(email);
        return new MyInfoResponse(foundUser.getEmail(), foundUser.getNickname());
    }

    @Transactional
    public void updateMyInfo(UpdateMyInfoRequest updateMyInfoRequest, String email) {
        User foundUser = getUserByEmail(email);

        String newPassword = updateMyInfoRequest.getPassword();
        String newNickname = updateMyInfoRequest.getNickname();

        // 비밀번호를 변경할 때
        if (newPassword != null) {
            // 값 검증
            userValidator.validatePassword(newPassword);
            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(newPassword);
            // 비밀번호 변경
            foundUser.changePassword(encodedPassword);
        }

        // 닉네임을 변경할 때
        if (newNickname != null) {
            // 값 검증
            userValidator.validateNickname(newNickname);
            // 닉네임 변경
            foundUser.changeNickname(newNickname);
        }
    }

    public void validateNickname(String nickname) {
        // 값 검증
        userValidator.validateNickname(nickname);
    }

    @Transactional
    public void deleteUser(String refreshToken, String email) {
        User foundUser = getUserByEmail(email);

        // 회원이 작성한 게시글 모두 삭제
        postRepository.deleteByAuthorNickname(foundUser.getNickname());

        // 데이터베이스에서 회원 삭제
        userRepository.delete(foundUser);

        // 값 검증
        validateRefreshToken(refreshToken);

        // 데이터베이스에 존재하는 해당 사용자의 모든 RefreshToken 삭제
        refreshTokenRepository.deleteByUserEmail(email);
    }

    private void validateRefreshToken(String refreshToken) {
        // 1. RefreshToken 헤더의 값이 올바른 형태인지 검증
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new ExpectedException(ErrorCode.NOT_REFRESH_TOKEN);
        }

        String token = refreshToken.split(" ")[1];

        // 2. 토큰이 RefreshToken인 지 검증
        String category = jwtProvider.getCategory(token);
        if (!category.equals("refresh")) {
            throw new ExpectedException(ErrorCode.NOT_REFRESH_TOKEN);
        }

        // 3. RefreshToken이 만료되었는 지 검증 1 -> 데이터베이스에 저장되어 있는지 여부로 확인
        if (!refreshTokenRepository.existsByRefreshToken(token)) {
            throw new ExpectedException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        // 4. RefreshToken이 만료되었는 지 검증 2 -> 토큰의 만료기간 확인
        // todo 추후 RefreshToken을 Redis에 저장한다면, 삭제해도 될 코드입니다.
        if (jwtProvider.isExpired(token)) {
            throw new ExpectedException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));
    }
}