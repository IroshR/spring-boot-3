package com.iroshnk.nftraffle.repository;

import com.iroshnk.nftraffle.entity.UserHasGroup;
import com.iroshnk.nftraffle.entity.UserHasGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserHasGroupRepository extends JpaRepository<UserHasGroup, UserHasGroupId> {
    List<UserHasGroup> findUserHasGroupByUserUserId(Long userId);

    void deleteByUserUserId(Long userId);
}
