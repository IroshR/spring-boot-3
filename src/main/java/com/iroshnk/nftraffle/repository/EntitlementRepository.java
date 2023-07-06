package com.iroshnk.nftraffle.repository;

import com.iroshnk.nftraffle.entity.Entitlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntitlementRepository extends JpaRepository<Entitlement, Long> {

    List<Entitlement> findEntitlementsByEntitlementIdIn(List<Long> entitlementIds);
}
