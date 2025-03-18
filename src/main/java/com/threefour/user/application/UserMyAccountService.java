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
    private final UserInfoValidator userInfoValidator;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
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
            userInfoValidator.validatePassword(newPassword);
            // 비밀번호는 암호화한 후 전달
            foundUser.changePassword(passwordEncoder.encode(newPassword));
            isUpdated = true;
        }

        if (updateMyInfoRequest.getNickname() != null) {
            String newNickname = updateMyInfoRequest.getNickname();
            userInfoValidator.validateNickname(newNickname);
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

        // DB에 존재하는 해당 사용자의 모든 RefreshToken 삭제
        refreshTokenRepository.deleteByUserEmail(email);
    }
}