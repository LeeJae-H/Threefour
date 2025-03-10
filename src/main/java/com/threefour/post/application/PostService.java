package com.threefour.post.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.post.domain.Post;
import com.threefour.post.domain.PostRepository;
import com.threefour.post.dto.EditPostRequest;
import com.threefour.post.dto.PostDetailsResponse;
import com.threefour.post.dto.PostSummaryResponse;
import com.threefour.post.dto.WritePostReqeust;
import com.threefour.user.domain.User;
import com.threefour.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public void writePost(WritePostReqeust writePostReqeust, String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));

        String authorNickname = foundUser.getNickname();
        String category = writePostReqeust.getCategory();
        String title = writePostReqeust.getTitle();
        String content = writePostReqeust.getContent();

        validateTitle(title);
        validateContent(content);

        Post newPost = Post.writePost(authorNickname, category, title, content);
        postRepository.save(newPost);
    }

    @Transactional
    public void editPost(Long postId, EditPostRequest editPostRequest, String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new ExpectedException(ErrorCode.POST_NOT_FOUND));

        // 작성자 본인인 지 확인
        if (!foundPost.getAuthorNickname().equals(foundUser.getNickname())) {
            throw new ExpectedException(ErrorCode.POST_ACCESS_DENIED);
        }

        // 게시글 수정이 이루어졌는지 여부
        boolean isUpdated = false;

        if (editPostRequest.getTitle() != null) {
            String newTitle = editPostRequest.getTitle();
            validateTitle(newTitle);
            foundPost.editTitle(newTitle);
            isUpdated = true;
        }

        if (editPostRequest.getContent() != null) {
            String newContent = editPostRequest.getContent();
            validateContent(newContent);
            foundPost.editContent(newContent);
            isUpdated = true;
        }

        if (isUpdated) {
            foundPost.updateUpdatedAt();
        }
    }

    @Transactional
    public void deletePost(Long postId, String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new ExpectedException(ErrorCode.POST_NOT_FOUND));

        // 작성자 본인인 지 확인
        if (!foundPost.getAuthorNickname().equals(foundUser.getNickname())) {
            throw new ExpectedException(ErrorCode.POST_ACCESS_DENIED);
        }

        postRepository.delete(foundPost);
    }

    // 게시글 제목은 (양쪽 끝의 공백 제거 후) 1~50자 이내여야 한다.
    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty() || title.length() > 50) {
            throw new ExpectedException(ErrorCode.INVALID_TITLE_LENGTH);
        }
    }

    // 게시글 내용은 (양쪽 끝의 공백 제거 후) 최소 1자 이상이어야 한다.
    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new ExpectedException(ErrorCode.INVALID_CONTENT_LENGTH);
        }
    }

    public PostDetailsResponse getPostDetails(Long postId, String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new ExpectedException(ErrorCode.POST_NOT_FOUND));

        // 본인 게시글 여부
        boolean isMine = foundPost.getAuthorNickname().equals(foundUser.getNickname());

        return new PostDetailsResponse(foundPost.getTitle(), foundPost.getContent(), foundPost.getAuthorNickname(), foundPost.getPostTimeInfo(), foundUser.getId(), isMine);
    }

    public List<PostSummaryResponse> getPostsList(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);

        return posts.stream()
                .map(post -> new PostSummaryResponse(post.getTitle(), post.getAuthorNickname(), post.getPostTimeInfo().getCreatedAt()))
                .collect(Collectors.toList());
    }
}