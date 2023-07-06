package com.iroshnk.nftraffle.repository;

import com.iroshnk.nftraffle.entity.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {
    Optional<UserLogin> findByUserName(String userName);

    UserLogin findByUserUserId(long userId);
}
