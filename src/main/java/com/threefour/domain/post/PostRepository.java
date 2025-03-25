package com.threefour.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface PostRepository extends Repository<Post, Long> {

    Post save(Post post);
    Optional<Post> findById(Long id);
    Page<Post> findAll(Pageable pageable);
    Page<Post> findAllByCategory(String category, Pageable pageable);
    void delete(Post post);

    @Modifying
    @Query("DELETE FROM Post p WHERE p.author.userId = :userId")
    void deleteByAuthor(Long userId);
}