package com.threefour.post.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PostTest {

    @Test
    @DisplayName("게시글 작성")
    void writePostTest() {
        // given
        String authorNickname = "테스트작성자닉네임";
        String category = "테스트게시판";
        String title = "테스트제목";
        String content = "테스트내용";

        // when
        Post newPost = Post.writePost(authorNickname, category, title, content);

        // then
        assertThat(newPost).isNotNull();
        assertThat(newPost.getAuthorNickname()).isEqualTo(authorNickname);
        assertThat(newPost.getCategory()).isEqualTo(category);
        assertThat(newPost.getTitle()).isEqualTo(title);
        assertThat(newPost.getContent()).isEqualTo(content);
        assertThat(newPost.getPostTimeInfo().getCreatedAt()).isNotNull();
        assertThat(newPost.getPostTimeInfo().getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("게시글 제목 수정")
    void editTitleTest() {
        // given
        String authorNickname = "테스트작성자닉네임";
        String category = "테스트게시판";
        String title = "테스트제목";
        String content = "테스트내용";

        Post post = Post.writePost(authorNickname, category, title, content);
        String newTitle = "새로운제목";

        // when
        post.editTitle(newTitle);

        // then
        assertThat(post.getTitle()).isEqualTo(newTitle);
    }

    @Test
    @DisplayName("게시글 내용 수정")
    void editContentTest() {
        // given
        String authorNickname = "테스트작성자닉네임";
        String category = "테스트게시판";
        String title = "테스트제목";
        String content = "테스트내용";

        Post post = Post.writePost(authorNickname, category, title, content);
        String newContent = "새로운내용";

        // when
        post.editContent(newContent);

        // then
        assertThat(post.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("수정일시 갱신")
    void updateUpdatedAtTest() {
        // given
        String authorNickname = "테스트작성자닉네임";
        String category = "테스트게시판";
        String title = "테스트제목";
        String content = "테스트내용";

        Post post = Post.writePost(authorNickname, category, title, content);
        LocalDateTime updatedAtBefore = post.getPostTimeInfo().getUpdatedAt();

        // when
        post.updateUpdatedAt();

        // then
        assertThat(post.getPostTimeInfo().getUpdatedAt()).isNotEqualTo(updatedAtBefore);
    }
}