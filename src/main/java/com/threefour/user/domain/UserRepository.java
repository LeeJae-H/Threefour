package com.threefour.user.domain;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {

    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    void delete(User user);
    Boolean existsByEmail(String email);
}
