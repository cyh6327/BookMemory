package com.yh.bookMemory.repository;

import com.yh.bookMemory.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users getReferenceByUserEmail(String userEmail);
    Users getReferenceByUserKey(Long userKey);

    Users saveAndFlush(Users users);
}
