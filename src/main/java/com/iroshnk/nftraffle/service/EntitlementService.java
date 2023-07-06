package com.iroshnk.nftraffle.service;

import com.iroshnk.nftraffle.entity.Entitlement;

import java.util.List;
import java.util.Set;

public interface EntitlementService {
    Set<String> getRolesByUser(Long userId);

    List<Entitlement> getEntitlements();

    List<Entitlement> getEntitlementsByIds(List<Long> entitlementIds);
}
