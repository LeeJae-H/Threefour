package com.threefour.domain;

import com.threefour.TestDatabaseConfig;
import com.threefour.domain.comment.Comment;
import com.threefour.domain.comment.CommentRepository;
import com.threefour.domain.user.User;
import com.threefour.dto.comment.CommentSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestDatabaseConfig.class)
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("truncate table comment"); // 테스트 전 데이터를 초기화
    }

    @Test
    @DisplayName("댓글 저장")
    void saveCommentTest() {
        Comment comment = createTestCommentInstance();

        // when
        Comment savedComment = commentRepository.save(comment);

        // then
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getId()).isNotNull(); // DB에 저장됐는지 여부 확인 -> DB에 저장되지 않으면 Id는 null 값
        assertThat(savedComment.getAuthor().getUserId()).isEqualTo(comment.getAuthor().getUserId());
        assertThat(savedComment.getPostId()).isEqualTo(comment.getPostId());
        assertThat(savedComment.getContent()).isEqualTo(comment.getContent());
    }

    @Test
    @DisplayName("Id로 댓글 조회")
    void findCommentByIdTest() {
        Comment comment = createTestCommentInstance();

        // given
        // DB에 댓글이 존재
        Comment savedComment = commentRepository.save(comment);

        // when
        Optional<Comment> foundComment = commentRepository.findById(savedComment.getId());

        // then
        assertThat(foundComment.isPresent()).isTrue();
        assertThat(foundComment.get().getAuthor().getUserId()).isEqualTo(savedComment.getAuthor().getUserId());
        assertThat(foundComment.get().getPostId()).isEqualTo(savedComment.getPostId());
        assertThat(foundComment.get().getContent()).isEqualTo(savedComment.getContent());
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteCommentTest() {
        Comment comment = createTestCommentInstance();

        // given
        // DB에 댓글이 존재
        Comment savedComment = commentRepository.save(comment);

        // when
        commentRepository.delete(savedComment);

        // then
        Optional<Comment> deletedComment = commentRepository.findById(savedComment.getId());
        boolean isExist = deletedComment.isPresent();
        assertThat(isExist).isFalse();
    }

    @Test
    @DisplayName("게시글의 모든 댓글 조회")
    void findCommentsByPostIdTest() {
        Comment comment1 = createTestCommentInstance();
        Comment comment2 = createTestCommentInstance();
        Comment comment3 = createTestCommentInstance();
        Long postId = comment1.getPostId();

        // given
        // DB에 댓글들이 존재
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        // when
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        // then
        assertThat(comments).hasSize(3);
        assertThat(comments.get(0).getPostId()).isEqualTo(postId);
        assertThat(comments.get(1).getPostId()).isEqualTo(postId);
        assertThat(comments.get(2).getPostId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("댓글 목록 조회 - 조인 쿼리")
    void findCommentSummaryTest() {
        Comment comment1 = createTestCommentInstance();
        Comment comment2 = createTestCommentInstance();
        Comment comment3 = createTestCommentInstance();
        Long postId = comment1.getPostId();

        // given
        // DB에 댓글들이 존재
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);
        // DB에 사용자가 존재
        User author = createTestUserAndSave();

        // when
        List<CommentSummary> commentSummaryList = commentRepository.findCommentSummary(postId);

        // then
        assertThat(commentSummaryList).isNotEmpty();
        assertThat(commentSummaryList.get(0).getCommentId()).isEqualTo(comment1.getId()); // get(0)과 comment1 비교를 통해 order by 간접적 검증
        assertThat(commentSummaryList.get(0).getNickname()).isEqualTo(author.getNickname()); // N+1 관련 쿼리 검증
        assertThat(commentSummaryList.get(0).getContent()).isEqualTo(comment1.getContent());
    }

    private Comment createTestCommentInstance() {
        Long userId = 1L;
        Long postId = 3L;
        String content = "테스트내용";
        return Comment.writeComment(userId, postId, content);
    }

    private User createTestUserAndSave() {
        String email = "test@naver.com";
        String encodedPassword = "testEncodedPassword";
        String nickname = "테스트닉네임";
        User user = User.join(email, encodedPassword, nickname);
        Long userId = 1L;

        // DB에 User 객체 저장
        String saveQuery = "INSERT INTO user (id, email, password, nickname, role, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(saveQuery,
                userId,
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getRole(),
                Timestamp.valueOf(user.getUserTimeInfo().getCreatedAt()),
                Timestamp.valueOf(user.getUserTimeInfo().getUpdatedAt())
        );
        return user;
    }
}
