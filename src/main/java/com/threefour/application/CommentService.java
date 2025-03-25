package com.threefour.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.domain.comment.Comment;
import com.threefour.domain.comment.CommentRepository;
import com.threefour.domain.user.User;
import com.threefour.domain.user.UserRepository;
import com.threefour.dto.comment.WriteCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void writeComment(WriteCommentRequest writeCommentRequest, String email) {
        User foundUser = getUserByEmail(email);
        Long postId = writeCommentRequest.getPostId();
        String content = writeCommentRequest.getContent();

        // 값 검증
        // 댓글 내용은 (양쪽 끝의 공백 제거 후) 1~50자 이내여야 한다.
        if (content == null || content.trim().isEmpty() || content.length() > 50) {
            throw new ExpectedException(ErrorCode.INVALID_COMMENT_LENGTH);
        }

        // 댓글 작성
        Comment newComment = Comment.writeComment(foundUser.getId(), postId, content);
        commentRepository.save(newComment);
    }

    @Transactional
    public void deleteComment(Long commentId, String email) {
        User foundUser = getUserByEmail(email);
        Comment foundComment = getCommentById(commentId);

        // 작성자 본인인 지 확인
        checkAuthor(foundUser, foundComment);

        // 댓글 삭제
        commentRepository.delete(foundComment);
    }

    private void checkAuthor(User user, Comment comment) {
        if (!comment.getUserId().equals(user.getId())) {
            throw new ExpectedException(ErrorCode.COMMENT_ACCESS_DENIED);
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));
    }

    private Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ExpectedException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
