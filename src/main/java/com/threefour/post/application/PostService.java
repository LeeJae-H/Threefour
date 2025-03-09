package com.threefour.post.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.post.domain.Post;
import com.threefour.post.domain.PostRepository;
import com.threefour.post.dto.PostCreateReqeust;
import com.threefour.user.domain.User;
import com.threefour.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public void createPost(PostCreateReqeust postCreateReqeust, String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));

        String authorNickname = foundUser.getNickname();
        String category = postCreateReqeust.getCategory();
        String title = postCreateReqeust.getTitle();
        String content = postCreateReqeust.getContent();

        Post newPost = Post.writePost(authorNickname, category, title, content);
        postRepository.save(newPost);
    }
}
