package com.yh.bookMemory.repository;

import com.yh.bookMemory.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRefreshTokenRepository extends JpaRepository<Users, Long> {
    UserRefreshTokenMapping getReferenceByUserKey(Long userKey);
}
