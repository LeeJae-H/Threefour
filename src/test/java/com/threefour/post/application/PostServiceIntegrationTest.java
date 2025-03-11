package com.threefour.post.application;

import com.threefour.common.ErrorCode;
import com.threefour.common.ExpectedException;
import com.threefour.post.domain.Post;
import com.threefour.post.dto.EditPostRequest;
import com.threefour.post.dto.WritePostReqeust;
import com.threefour.user.application.UserAccountServiceIntegrationTest;
import com.threefour.user.domain.User;
import com.threefour.user.dto.UpdateUserInfoRequest;
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
import java.time.LocalDateTime;

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

    @Test
    @DisplayName("게시글 수정 성공 - 모두 유효한 입력값, 모든 정보 수정")
    void editPost_ByValidInput_Then_Success() {
        String newTitle = "새로운제목";
        String newContent = "새로운내용";

        // given
        // DB에 사용자(작성자)가 존재
        User author = createTestUserAndSave();
        // DB에 게시글이 존재
        Post post = createTestPostInstance(author.getNickname());
        Post savedPost = savePost(post);
        LocalDateTime updatedAtBefore = savedPost.getPostTimeInfo().getUpdatedAt();
        Long postId = savedPost.getId();

        // when
        postService.editPost(postId, new EditPostRequest(newTitle, newContent),author.getEmail());

        // then
        String foundPostQuery = "SELECT author_nickname, category, title, content FROM post WHERE id = ?";
        Post foundPost = jdbcTemplate.queryForObject(foundPostQuery, new PostRowMapper(), postId);
        assertThat(foundPost).isNotNull();

        // 1. 제목이 변경되었는지 확인
        assertThat(foundPost.getTitle()).isEqualTo(newTitle);

        // 2. 내용이 변경되었는지 확인
        assertThat(foundPost.getContent()).isEqualTo(newContent);

        // 3. 수정일시가 갱신되었는지 확인
        assertThat(foundPost.getPostTimeInfo().getUpdatedAt()).isNotEqualTo(updatedAtBefore);
    }

    @Test
    @DisplayName("게시글 수정 성공 - 모두 유효한 입력값, 제목만 수정")
    void editPost_ByValidInput_OnlyTitle_Then_Success() {
        String newTitle = "새로운제목";

        // given
        // DB에 사용자(작성자)가 존재
        User author = createTestUserAndSave();
        // DB에 게시글이 존재
        Post post = createTestPostInstance(author.getNickname());
        Post savedPost = savePost(post);
        LocalDateTime updatedAtBefore = savedPost.getPostTimeInfo().getUpdatedAt();
        Long postId = savedPost.getId();

        // when
        postService.editPost(postId, new EditPostRequest(newTitle, null),author.getEmail());

        // then
        String foundPostQuery = "SELECT author_nickname, category, title, content FROM post WHERE id = ?";
        Post foundPost = jdbcTemplate.queryForObject(foundPostQuery, new PostRowMapper(), postId);
        assertThat(foundPost).isNotNull();

        // 1. 제목이 변경되었는지 확인
        assertThat(foundPost.getTitle()).isEqualTo(newTitle);

        // 2. 내용은 변경되지 않았는지 확인
        assertThat(foundPost.getContent()).isEqualTo(savedPost.getContent());

        // 3. 수정일시가 갱신되었는지 확인
        assertThat(foundPost.getPostTimeInfo().getUpdatedAt()).isNotEqualTo(updatedAtBefore);
    }

    @Test
    @DisplayName("게시글 수정 실패 - 다른 사용자가 수정하려고 할 때 예외 발생")
    void editPost_FromAnotherUser_Then_Exception() {
        // given
        // DB에 사용자(작성자)가 존재
        User author = createTestUserAndSave();
        // DB에 게시글이 존재
        Post post = createTestPostInstance(author.getNickname());
        Post savedPost = savePost(post);
        Long postId = savedPost.getId();
        // DB에 사용자(작성자가 아닌 사용자)가 존재
        String anotherUserEmail = author.getEmail() + "a";
        String anotherEncodedPassword = "testEncodedPassword";
        String anotherNickname = "고유 닉네임";
        User anotherUser = User.join(anotherUserEmail, anotherEncodedPassword, anotherNickname);
        String saveQuery = "INSERT INTO user (email, password, nickname, role, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(saveQuery,
                anotherUser.getEmail(),
                anotherUser.getPassword(),
                anotherUser.getNickname(),
                anotherUser.getRole(),
                Timestamp.valueOf(anotherUser.getUserTimeInfo().getCreatedAt()),
                Timestamp.valueOf(anotherUser.getUserTimeInfo().getUpdatedAt())
        );

        // when & then
        assertThatThrownBy(() -> postService.editPost(postId, new EditPostRequest("새로운제목", "새로운내용"), anotherUser.getEmail()))
                .isInstanceOf(ExpectedException.class)
                .satisfies(e -> {
                    ExpectedException ex = (ExpectedException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.POST_ACCESS_DENIED);
                });
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deletePost_Success() {
        // given
        // DB에 사용자(작성자)가 존재
        User author = createTestUserAndSave();
        // DB에 게시글이 존재
        Post post = createTestPostInstance(author.getNickname());
        Post savedPost = savePost(post);
        Long postId = savedPost.getId();

        // when
        postService.deletePost(postId, author.getEmail());

        // then
        String checkPostQuery = "SELECT COUNT(*) FROM post WHERE id = ?";
        Integer postCount = jdbcTemplate.queryForObject(checkPostQuery, Integer.class, postId);
        assertThat(postCount).isEqualTo(0);;
    }

    private Post createTestPostInstance(String authorNickname) {
        String category = "테스트게시판";
        String title = "테스트제목";
        String content = "테스트내용";
        return Post.writePost(authorNickname, category, title, content);
    }

    private Post savePost(Post post) {
        // DB에 Post 객체 저장
        String saveQuery = "INSERT INTO post (author_nickname, category, title, content, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(saveQuery,
                post.getAuthorNickname(),
                post.getCategory(),
                post.getTitle(),
                post.getContent(),
                Timestamp.valueOf(post.getPostTimeInfo().getCreatedAt()),
                Timestamp.valueOf(post.getPostTimeInfo().getUpdatedAt())
        );

            // DB로부터 postId 가져옴
            String getIdQuery = "SELECT id FROM post " +
                    "WHERE title = ? AND content = ? AND author_nickname = ? " +
                    "ORDER BY created_at DESC " +
                    "LIMIT 1";                    // todo unique 키가 없어서 임시 조회 쿼리
            Long postId = jdbcTemplate.queryForObject(getIdQuery, Long.class, post.getTitle(), post.getContent(), post.getAuthorNickname());

        // Post 객체에 postId 값 반영
        ReflectionTestUtils.setField(post, "id", postId);

        return post;
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