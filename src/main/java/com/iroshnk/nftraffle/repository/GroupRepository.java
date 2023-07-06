package com.iroshnk.nftraffle.repository;

import com.iroshnk.nftraffle.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findByGroupIdIn(List<Long> groupIds);

    Optional<Group> findByGroupName(String groupName);
}
