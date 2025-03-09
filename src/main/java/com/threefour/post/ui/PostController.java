package com.threefour.post.ui;

import com.threefour.common.ApiResponse;
import com.threefour.post.application.PostService;
import com.threefour.post.dto.PostCreateReqeust;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    /**
     * 게시글 생성 API
     *
     * @param postCreateReqeust
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createPost(@RequestBody PostCreateReqeust postCreateReqeust) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        postService.createPost(postCreateReqeust, email);
        return ApiResponse.success("ok");
    }
}