package com.iroshnk.nftraffle.repository;

import com.iroshnk.nftraffle.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    UserSession getUserSessionByTokenAndUserLoginUserName(String token, String username);
}
