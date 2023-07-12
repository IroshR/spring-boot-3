package com.iroshnk.nftraffle.repository;

import com.iroshnk.nftraffle.entity.EntitlementHasRole;
import com.iroshnk.nftraffle.entity.EntitlementHasRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface EntitlementHasRoleRepository extends JpaRepository<EntitlementHasRole, EntitlementHasRoleId> {
    List<EntitlementHasRole> findEntitlementHasRolesByEntitlementEntitlementIdIn(List<Long> entitlementIds);

    @Query("SELECT ge.role.roleName FROM EntitlementHasRole ge WHERE ge.entitlement.entitlementId IN :entitlementIds")
    Set<String> findRoleNamesByEntitlementIds(@Param("entitlementIds") List<Long> entitlementIds);
}
