package com.threefour.post.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface PostRepository extends Repository<Post, Long> {

    Post save(Post post);
    Optional<Post> findById(Long id);
    Page<Post> findAll(Pageable pageable);
    void delete(Post post);
    void deleteByAuthorNickname(String authorNickname);
}