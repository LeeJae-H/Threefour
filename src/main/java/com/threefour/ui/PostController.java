package com.threefour.ui;

import com.threefour.common.ApiResponse;
import com.threefour.application.PostService;
import com.threefour.dto.post.EditPostRequest;
import com.threefour.dto.post.PostDetailsResponse;
import com.threefour.dto.post.PostsListResponse;
import com.threefour.dto.post.WritePostReqeust;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    /**
     * 게시글 작성 API
     *
     * @param writePostReqeust
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> writePost(@RequestBody WritePostReqeust writePostReqeust) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        postService.writePost(writePostReqeust, email);
        return new ResponseEntity<>(ApiResponse.success("success"), HttpStatus.OK);
    }

    /**
     * 게시글 상세 조회 API
     *
     * 작성자가 본인 게시글을 상세 조회하는 경우에,
     * isMine을 true로 응답합니다. 이를 위해,
     * 선택적으로 AccessToken을 요청으로 받습니다.
     *
     * @param postId
     * @param accessToken
     * @return PostDetailsResponse
     */
    @GetMapping("/{postId}/details")
    public ResponseEntity<ApiResponse<PostDetailsResponse>> getPostDetails(
            @PathVariable Long postId,
            @RequestHeader(value = "AccessToken", required = false) String accessToken
    ) {
        PostDetailsResponse postDetailsResponse = postService.getPostDetails(postId, accessToken);
        return new ResponseEntity<>(ApiResponse.success(postDetailsResponse), HttpStatus.OK);
    }

    /**
     * 게시글 수정 API
     * 작성자 본인만 가능합니다.
     *
     * @param postId
     * @param editPostRequest
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> editPost(
            @PathVariable Long postId,
            @RequestBody EditPostRequest editPostRequest
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        postService.editPost(postId, editPostRequest, email);
        return new ResponseEntity<>(ApiResponse.success("success"), HttpStatus.OK);
    }

    /**
     * 게시글 삭제 API
     * 작성자 본인만 가능합니다.
     *
     * @param postId
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable Long postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        postService.deletePost(postId, email);
        return new ResponseEntity<>(ApiResponse.success("success"), HttpStatus.OK);
    }

    /**
     * 게시글 목록 조회 API
     *
     * 홈 화면에서 보여지는 게시글 목록으로, 페이지 단위로 조회합니다.
     *
     * @param page
     * @param size
     * @return PostsListResponse
     */
    @GetMapping("/list/all")
    public ResponseEntity<ApiResponse<PostsListResponse>> getPostsList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        PostsListResponse postsList = postService.getPostsList(pageable);
        return new ResponseEntity<>(ApiResponse.success(postsList), HttpStatus.OK);
    }

    /**
     * 게시판의 게시글 목록 조회 API
     *
     * 페이지 단위로 조회합니다.
     *
     * @param page
     * @param size
     * @return PostsListResponse
     */
    @GetMapping("/list/all/{category}")
    public ResponseEntity<ApiResponse<PostsListResponse>> getPostsListByCategory(
            @PathVariable String category,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        PostsListResponse postsList = postService.getPostsListByCategory(category, pageable);
        return new ResponseEntity<>(ApiResponse.success(postsList), HttpStatus.OK);
    }
}