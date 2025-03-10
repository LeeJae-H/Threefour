package com.threefour.post.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface PostRepository extends Repository<Post, Long> {

    void save(Post post);
    Optional<Post> findById(Long id);
    void delete(Post post);
    void deleteByAuthorNickname(String authorNickname);
    Page<Post> findAll(Pageable pageable);
}