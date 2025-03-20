package com.threefour.application.user;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.domain.user.User;
import com.threefour.domain.user.UserRepository;
import com.threefour.dto.user.OtherUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public OtherUserInfoResponse getOtherUserInfo(Long userId) {
        User otherUser = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));

        return new OtherUserInfoResponse(otherUser.getNickname());
    }
}