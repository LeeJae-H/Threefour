package com.threefour.post.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("truncate table user"); // 테스트 전 데이터를 초기화
    }

    @Test
    @DisplayName("게시글 저장")
    void savePostTest() {
        String authorNickname = "테스트작성자닉네임";
        String category = "테스트게시판";
        String title = "테스트제목";
        String content = "테스트내용";
        Post post = Post.writePost(authorNickname, category, title, content);

        // when
        Post savedPost = postRepository.save(post);

        // then
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getId()).isNotNull(); // DB에 저장됐는지 여부 확인 -> DB에 저장되지 않으면 Id는 null 값
        assertThat(savedPost.getAuthorNickname()).isEqualTo(authorNickname);
        assertThat(savedPost.getCategory()).isEqualTo(category);
        assertThat(savedPost.getTitle()).isEqualTo(title);
        assertThat(savedPost.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("Id로 게시글 조회")
    void findPostByIdTest() {
        String authorNickname = "테스트작성자닉네임";
        String category = "테스트게시판";
        String title = "테스트제목";
        String content = "테스트내용";
        Post post = Post.writePost(authorNickname, category, title, content);

        // given
        // DB에 게시글이 존재
        Post savedPost = postRepository.save(post);

        // when
        Optional<Post> foundPost = postRepository.findById(savedPost.getId());

        // then
        assertThat(foundPost.isPresent()).isTrue();
        assertThat(foundPost.get().getAuthorNickname()).isEqualTo(authorNickname);
        assertThat(foundPost.get().getCategory()).isEqualTo(category);
        assertThat(foundPost.get().getTitle()).isEqualTo(title);
        assertThat(foundPost.get().getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("전체 게시글 페이지 단위로 조회")
    void findAllPostsByPageTest() {
        String authorNickname = "테스트작성자닉네임";
        String category = "테스트게시판";
        String title = "테스트제목";
        String content = "테스트내용";
        Post post1 = Post.writePost(authorNickname, category, title, content);
        Post post2 = Post.writePost(authorNickname, category, title, content);
        Post post3 = Post.writePost(authorNickname, category, title, content);

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
    @DisplayName("게시글 삭제")
    void deletePostTest() {
        String authorNickname = "테스트작성자닉네임";
        String category = "테스트게시판";
        String title = "테스트제목";
        String content = "테스트내용";
        Post post = Post.writePost(authorNickname, category, title, content);

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

    @Test
    @DisplayName("작성자 닉네임으로 해당 작성자의 모든 게시글 삭제")
    void deletePostByAuthorNicknameTest() {
        String authorNickname = "테스트작성자닉네임";
        String category = "테스트게시판";
        String title = "테스트제목";
        String content = "테스트내용";
        Post post1 = Post.writePost(authorNickname, category, title, content);
        Post post2 = Post.writePost(authorNickname, category, title, content);

        // given
        // DB에 게시글들이 존재
        Post savedPost1 = postRepository.save(post1);
        Post savedPost2 = postRepository.save(post2);

        // when
        postRepository.deleteByAuthorNickname(authorNickname);

        // then
        assertThat(postRepository.findById(savedPost1.getId())).isEmpty();
        assertThat(postRepository.findById(savedPost2.getId())).isEmpty();
    }
}