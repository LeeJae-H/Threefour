package com.threefour.user.domain;

import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<User, Long> {
    Boolean existsByEmail(String email);
    User save(User user);
    User findByEmail(String email);
}
