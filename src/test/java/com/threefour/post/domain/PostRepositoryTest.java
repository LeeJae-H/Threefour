package com.threefour.post.domain;

import com.threefour.domain.post.Post;
import com.threefour.domain.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("truncate table post"); // 테스트 전 데이터를 초기화
    }

    @Test
    @DisplayName("게시글 저장")
    void savePostTest() {
        Post post = createTestPostInstance();

        // when
        Post savedPost = postRepository.save(post);

        // then
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getId()).isNotNull(); // DB에 저장됐는지 여부 확인 -> DB에 저장되지 않으면 Id는 null 값
        assertThat(savedPost.getAuthor().getNickname()).isEqualTo(post.getAuthor().getNickname());
        assertThat(savedPost.getCategory()).isEqualTo(post.getCategory());
        assertThat(savedPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(savedPost.getContent()).isEqualTo(post.getContent());
    }

    @Test
    @DisplayName("Id로 게시글 조회")
    void findPostByIdTest() {
        Post post = createTestPostInstance();

        // given
        // DB에 게시글이 존재
        Post savedPost = postRepository.save(post);

        // when
        Optional<Post> foundPost = postRepository.findById(savedPost.getId());

        // then
        assertThat(foundPost.isPresent()).isTrue();
        assertThat(foundPost.get().getAuthor().getNickname()).isEqualTo(savedPost.getAuthor().getNickname());
        assertThat(foundPost.get().getCategory()).isEqualTo(savedPost.getCategory());
        assertThat(foundPost.get().getTitle()).isEqualTo(savedPost.getTitle());
        assertThat(foundPost.get().getContent()).isEqualTo(savedPost.getContent());
    }

    @Test
    @DisplayName("전체 게시글 페이지 단위로 조회")
    void findAllPostsByPageTest() {
        Post post1 = createTestPostInstance();
        Post post2 = createTestPostInstance();
        Post post3 = createTestPostInstance();

        // given
        // DB에 게시글들이 존재
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // when
        Page<Post> page = postRepository.findAll(PageRequest.of(0, 2));

        // then
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("게시판 게시글 페이지 단위로 조회")
    void findCategoryPostsByPageTest() {
        Post post1 = createTestPostInstance();
        Post post2 = createTestPostInstance();
        Post post3 = createTestPostInstance();

        // given
        // DB에 게시글들이 존재
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // when
        Page<Post> page = postRepository.findAllByCategory(post1.getCategory(), PageRequest.of(0, 2));

        // then
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("게시글 삭제")
    void deletePostTest() {
        Post post = createTestPostInstance();

        // given
        // DB에 게시글이 존재
        Post savedPost = postRepository.save(post);

        // when
        postRepository.delete(savedPost);

        // then
        Optional<Post> deletedPost = postRepository.findById(savedPost.getId());
        boolean isExist = deletedPost.isPresent();
        assertThat(isExist).isFalse();
    }

    // JPQL은 DB의 데이터는 바로 삭제하지만, 영속성 컨텍스트에 남아 있는 엔티티는 삭제하지 않는다.
    // 따라서, 삭제 이후 findById로 조회 시 영속성 컨텍스트에서 조회한다.
    @Test
    @DisplayName("작성자 닉네임으로 해당 작성자의 모든 게시글 삭제")
    void deletePostByAuthorNicknameTest() {
        Post post1 = createTestPostInstance();
        Post post2 = createTestPostInstance();
        String authorNickname = post1.getAuthor().getNickname();

        // given
        // DB에 게시글들이 존재
        postRepository.save(post1);
        postRepository.save(post2);

        // when
        postRepository.deleteByAuthor(authorNickname);

        // then
        String query = "SELECT COUNT(*) FROM post WHERE author_nickname = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, authorNickname);
        assertThat(count).isZero();
    }

    private Post createTestPostInstance() {
        String authorNickname = "테스트작성자닉네임";
        String category = "테스트게시판";
        String title = "테스트제목";
        String content = "테스트내용";
        return Post.writePost(authorNickname, category, title, content);
    }
}