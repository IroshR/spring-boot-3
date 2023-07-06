package com.iroshnk.nftraffle.repository;

import com.iroshnk.nftraffle.entity.EntitlementHasRole;
import com.iroshnk.nftraffle.entity.EntitlementHasRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntitlementHasRoleRepository extends JpaRepository<EntitlementHasRole, EntitlementHasRoleId> {
    List<EntitlementHasRole> findEntitlementHasRolesByEntitlementEntitlementIdIn(List<Long> entitlementIds);
}
