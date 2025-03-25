package com.threefour.ui;

import com.threefour.application.CommentService;
import com.threefour.common.ApiResponse;
import com.threefour.dto.comment.WriteCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성 API
     *
     * @param writeCommentRequest
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> writeComment(@RequestBody WriteCommentRequest writeCommentRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        commentService.writeComment(writeCommentRequest, email);
        return new ResponseEntity<>(ApiResponse.success("success"), HttpStatus.OK);
    }

    /**
     * 댓글 삭제 API
     * 작성자 본인만 가능합니다.
     *
     * @param commentId
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable Long commentId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        commentService.deleteComment(commentId, email);
        return new ResponseEntity<>(ApiResponse.success("success"), HttpStatus.OK);
    }
}
