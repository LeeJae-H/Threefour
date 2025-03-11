package com.threefour.post.ui;

import com.threefour.common.ApiResponse;
import com.threefour.post.application.PostService;
import com.threefour.post.dto.EditPostRequest;
import com.threefour.post.dto.PostDetailsResponse;
import com.threefour.post.dto.PostSummaryResponse;
import com.threefour.post.dto.WritePostReqeust;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시글 작성 API
     *
     * @param writePostReqeust
     * @return 게시글 id
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> writePost(@RequestBody WritePostReqeust writePostReqeust) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long postId = postService.writePost(writePostReqeust, email);
        return ApiResponse.success(postId);
    }

    /**
     * 게시글 수정 API
     *
     * 작성자 본인만 가능합니다.
     *
     * @param postId
     * @param editPostRequest
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> editPost(
            @PathVariable Long postId,
            @RequestBody EditPostRequest editPostRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        postService.editPost(postId, editPostRequest, email);
        return ApiResponse.success("ok");
    }

    /**
     * 게시글 삭제 API
     *
     * 작성자 본인만 가능합니다.
     *
     * @param postId
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable Long postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        postService.deletePost(postId, email);
        return ApiResponse.success("ok");
    }

    /**
     * 게시글 세부 사항 조회 API
     *
     * @param postId
     * @return PostDetailsResponse
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailsResponse>> getPostDetails(@PathVariable Long postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        PostDetailsResponse postDetailsResponse = postService.getPostDetails(postId, email);
        return ApiResponse.success(postDetailsResponse);
    }

    /**
     * 게시글 목록 조회 API
     *
     * 홈 화면에서 보여지는 게시글 목록입니다.
     * 페이지 단위로 조회합니다.
     *
     * @param page
     * @param size
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostSummaryResponse>>> getPostsList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        List<PostSummaryResponse> postsList = postService.getPostsList(pageable);
        return ApiResponse.success(postsList);
    }
}