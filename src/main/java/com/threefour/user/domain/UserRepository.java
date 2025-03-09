package com.threefour.user.domain;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    Boolean existsByEmail(String email);
    User save(User user);
    Optional<User> findByEmail(String email);
    void delete(User user);
}
