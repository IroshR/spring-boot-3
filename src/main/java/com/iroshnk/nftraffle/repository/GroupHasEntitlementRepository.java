package com.iroshnk.nftraffle.repository;

import com.iroshnk.nftraffle.entity.GroupHasEntitlement;
import com.iroshnk.nftraffle.entity.GroupHasEntitlementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupHasEntitlementRepository extends JpaRepository<GroupHasEntitlement, GroupHasEntitlementId> {

    List<GroupHasEntitlement> getGroupHasEntitlementByGroupGroupIdIn(List<Long> groupIds);

    @Query("SELECT ge.entitlement.entitlementId FROM GroupHasEntitlement ge WHERE ge.group.groupId IN :groupIds")
    List<Long> findEntitlementIdsByGroupIds(@Param("groupIds") List<Long> groupIds);

    List<GroupHasEntitlement> getGroupHasEntitlementByGroupGroupId(Long groupId);

    void deleteByGroupGroupId(Long groupId);
}
