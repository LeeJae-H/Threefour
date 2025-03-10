package com.threefour.user.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.user.domain.User;
import com.threefour.user.domain.UserRepository;
import com.threefour.user.dto.MyUserInfoResponse;
import com.threefour.user.dto.OtherUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public MyUserInfoResponse getMyUserInfo(Long userId, String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));

        // 본인인 지 확인
        if (!foundUser.getId().equals(userId)) {
            throw new ExpectedException(ErrorCode.USER_ACCOUNT_ACCESS_DENIED);
        }

        return new MyUserInfoResponse(foundUser.getEmail(), foundUser.getNickname());
    }

    public OtherUserInfoResponse getOtherUserInfo(Long userId) {
        User otherUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));

        return new OtherUserInfoResponse(otherUser.getNickname());
    }
}