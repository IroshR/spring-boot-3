package com.iroshnk.nftraffle.repository;

import com.iroshnk.nftraffle.entity.UserHasGroup;
import com.iroshnk.nftraffle.entity.UserHasGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserHasGroupRepository extends JpaRepository<UserHasGroup, UserHasGroupId> {
    List<UserHasGroup> findUserHasGroupByUserUserId(Long userId);

    @Query("SELECT ug.group.groupId FROM UserHasGroup ug WHERE ug.user.userId = :userId")
    List<Long> findGroupIdsByUserId(@Param("userId") Long userId);

    void deleteByUserUserId(Long userId);
}
