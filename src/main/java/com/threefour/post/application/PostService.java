package com.threefour.post.application;

import com.threefour.auth.JwtProvider;
import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.post.domain.Post;
import com.threefour.post.domain.PostRepository;
import com.threefour.post.dto.*;
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
    private final JwtProvider jwtProvider;

    @Transactional
    public void writePost(WritePostReqeust writePostReqeust, String email) {
        User foundUser = getUserByEmail(email);
        String authorNickname = foundUser.getNickname();
        String category = writePostReqeust.getCategory();
        String title = writePostReqeust.getTitle();
        String content = writePostReqeust.getContent();

        // 값 검증
        PostValidator.validateTitle(title);
        PostValidator.validateContent(content);

        // 게시글 작성
        Post newPost = Post.writePost(authorNickname, category, title, content);
        postRepository.save(newPost);
    }

    @Transactional
    public void editPost(Long postId, EditPostRequest editPostRequest, String email) {
        User foundUser = getUserByEmail(email);
        Post foundPost = getPostById(postId);

        // 작성자 본인인 지 확인
        checkAuthor(foundUser, foundPost);

        String newTitle = editPostRequest.getTitle();
        String newContent = editPostRequest.getContent();

        // 제목을 변경할 때
        if (newTitle != null) {
            // 값 검증
            PostValidator.validateTitle(newTitle);
            // 제목 변경
            foundPost.editTitle(newTitle);
        }

        // 내용을 변경할 때
        if (newContent != null) {
            // 값 검증
            PostValidator.validateContent(newContent);
            // 내용 변경
            foundPost.editContent(newContent);
        }
    }

    @Transactional
    public void deletePost(Long postId, String email) {
        User foundUser = getUserByEmail(email);
        Post foundPost = getPostById(postId);

        // 작성자 본인인 지 확인
        checkAuthor(foundUser, foundPost);

        // 게시글 삭제
        postRepository.delete(foundPost);
    }

    private void checkAuthor(User user, Post post) {
        if (!post.getAuthorNickname().equals(user.getNickname())) {
            throw new ExpectedException(ErrorCode.POST_ACCESS_DENIED);
        }
    }

    public PostDetailsResponse getPostDetails(Long postId, String accessToken) {
        Post foundPost = getPostById(postId);

        // 조회한 사람이 게시글 작성자인지 여부
        boolean isMine = checkIfUserIsAuthor(foundPost, accessToken);

        return new PostDetailsResponse(foundPost.getAuthorNickname(), foundPost.getCategory(), foundPost.getTitle(), foundPost.getContent(), foundPost.getPostTimeInfo(), isMine);
    }

    private boolean checkIfUserIsAuthor(Post foundPost, String accessToken) {
        // 1. AccessToken 헤더의 값이 올바른 형태인지 검증
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            return false;
        }

        String token = accessToken.split(" ")[1];

        // 2. 토큰이 AccessToken인 지 검증
        String category = jwtProvider.getCategory(token);
        if (!category.equals("access")) {
            return false;
        }

        // 3. AccessToken이 만료되었는 지 검증
        if (jwtProvider.isExpired(token)) {
            return false;
        }

        String email = jwtProvider.getEmail(token);
        User foundUser = getUserByEmail(email);

        return foundPost.getAuthorNickname().equals(foundUser.getNickname());
    }

    public PostsListResponse getPostsList(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);

        List<PostSummary> postSummaryList = posts.stream()
                .map(post -> new PostSummary(post.getId(), post.getTitle(), post.getAuthorNickname(), post.getPostTimeInfo().getCreatedAt()))
                .collect(Collectors.toList());

        return new PostsListResponse(postSummaryList, posts.getTotalPages());
    }

    public PostsListResponse getPostsListByCategory(String category, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByCategory(category, pageable);

        List<PostSummary> postSummaryList = posts.stream()
                .map(post -> new PostSummary(post.getId(), post.getTitle(), post.getAuthorNickname(), post.getPostTimeInfo().getCreatedAt()))
                .collect(Collectors.toList());

        return new PostsListResponse(postSummaryList, posts.getTotalPages());
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));
    }

    private Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ExpectedException(ErrorCode.POST_NOT_FOUND));
    }
}