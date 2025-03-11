package com.threefour.post.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.post.domain.Post;
import com.threefour.post.dto.WritePostReqeust;
import com.threefour.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
public class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static class PostRowMapper implements RowMapper<Post> {
        @Override
        public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
            // 생성자가 없어 정적 팩토리 메서드 사용
            return Post.writePost(
                    rs.getString("author_nickname"),
                    rs.getString("category"),
                    rs.getString("title"),
                    rs.getString("content")
            );
        }
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("truncate table post"); // 테스트 전 데이터를 초기화
        jdbcTemplate.update("truncate table user"); // 테스트 전 데이터를 초기화
    }

    @Test
    @DisplayName("게시글 작성 성공 - 모두 유효한 입력값")
    void writePost_ByValidInput_Then_Success() {
        String inputCategory = "테스트게시판";
        String inputTitle = "테스트제목";
        String inputContent = "테스트내용";

        // given
        // DB에 사용자(작성자)가 존재
        User author = createTestUserAndSave();

        // when
        Long postId = postService.writePost(new WritePostReqeust(inputCategory, inputTitle, inputContent), author.getEmail());

        // then
        // DB에 데이터가 저장되었는지 확인
        String query = "SELECT author_nickname, category, title, content FROM post WHERE id = ?";
        Post savedPost = jdbcTemplate.queryForObject(query, new PostRowMapper(), postId);
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getAuthorNickname()).isEqualTo(author.getNickname());
        assertThat(savedPost.getCategory()).isEqualTo(inputCategory);
        assertThat(savedPost.getTitle()).isEqualTo(inputTitle);
        assertThat(savedPost.getContent()).isEqualTo(inputContent);
    }

    @Test
    @DisplayName("게시글 작성 실패 - 제목 값 검증 실패 시 예외 발생")
    void writePost_ByInvalidTitle_Then_Exception() {
        String inputNullTitle = null;
        String inputWhiteSpaceTitle = " ";
        String inputLongTitle = "a".repeat(51); // 51자

        // given
        // DB에 사용자(작성자)가 존재
        User author = createTestUserAndSave();

        // when & then
        // 1. null 값의 제목
        assertThatThrownBy(() -> postService.writePost(new WritePostReqeust("테스트게시판", inputNullTitle, "테스트내용"), author.getEmail()))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_TITLE_LENGTH);
                });
        // 2. 양쪽 끝의 공백 제거 후 1자 미만인 제목
        assertThatThrownBy(() -> postService.writePost(new WritePostReqeust("테스트게시판", inputWhiteSpaceTitle, "테스트내용"), author.getEmail()))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_TITLE_LENGTH);
                });
        // 3. 양쪽 끝의 공백 제거 후 51자 이상인 제목
        assertThatThrownBy(() -> postService.writePost(new WritePostReqeust("테스트게시판", inputLongTitle, "테스트내용"), author.getEmail()))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_TITLE_LENGTH);
                });
    }

    @Test
    @DisplayName("게시글 작성 실패 - 내용 값 검증 실패 시 예외 발생")
    void writePost_ByInvalidContent_Then_Exception() {
        String inputNullContent = null;
        String inputWhiteSpaceContent = " ";

        // given
        // DB에 사용자(작성자)가 존재
        User author = createTestUserAndSave();

        // when & then
        // 1. null 값의 내용
        assertThatThrownBy(() -> postService.writePost(new WritePostReqeust("테스트게시판", "테스트제목", inputNullContent), author.getEmail()))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_CONTENT_LENGTH);
                });
        // 2. 양쪽 끝의 공백 제거 후 1자 미만인 내용
        assertThatThrownBy(() -> postService.writePost(new WritePostReqeust("테스트게시판", "테스트제목", inputWhiteSpaceContent), author.getEmail()))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_CONTENT_LENGTH);
                });
    }

    private Post createTestPostInstance() {
        String authorNickname = "테스트작성자닉네임";
        String category = "테스트게시판";
        String title = "테스트제목";
        String content = "테스트내용";
        return Post.writePost(authorNickname, category, title, content);
    }

    private User createTestUserAndSave() {
        String email = "test@naver.com";
        String encodedPassword = "testEncodedPassword";
        String nickname = "테스트닉네임"; // 처음과 끝에 공백을 넣으면 안됩니다.
        User user = User.join(email, encodedPassword, nickname);

        // DB에 User 객체 저장
        String saveQuery = "INSERT INTO user (email, password, nickname, role, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(saveQuery,
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getRole(),
                Timestamp.valueOf(user.getUserTimeInfo().getCreatedAt()),
                Timestamp.valueOf(user.getUserTimeInfo().getUpdatedAt())
        );

        // DB로부터 userId 가져옴
        String getIdQuery = "SELECT id FROM user WHERE email = ?";  // email은 unique 제약 조건
        Long userId = jdbcTemplate.queryForObject(getIdQuery, Long.class, user.getEmail());

        // User 객체에 userId 값 반영
        ReflectionTestUtils.setField(user, "id", userId);

        return user;
    }
}