package com.iroshnk.nftraffle.repository;

import com.iroshnk.nftraffle.entity.GroupHasEntitlement;
import com.iroshnk.nftraffle.entity.GroupHasEntitlementId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupHasEntitlementRepository extends JpaRepository<GroupHasEntitlement, GroupHasEntitlementId> {

    List<GroupHasEntitlement> getGroupHasEntitlementByGroupGroupIdIn(List<Long> groupIds);

    List<GroupHasEntitlement> getGroupHasEntitlementByGroupGroupId(Long groupId);

    void deleteByGroupGroupId(Long groupId);
}
