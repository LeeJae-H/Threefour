package com.threefour.post.ui;

import com.threefour.common.ApiResponse;
import com.threefour.post.application.PostService;
import com.threefour.post.dto.EditPostRequest;
import com.threefour.post.dto.WritePostReqeust;
import com.threefour.user.dto.UpdateUserInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
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
        return ApiResponse.success("ok");
    }

    /**
     * 게시글 수정 API
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
     * @param postId
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable Long postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        postService.deletePost(postId, email);
        return ApiResponse.success("ok");
    }
}