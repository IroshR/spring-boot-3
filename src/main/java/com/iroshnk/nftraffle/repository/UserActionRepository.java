package com.iroshnk.nftraffle.repository;

import com.iroshnk.nftraffle.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
}
