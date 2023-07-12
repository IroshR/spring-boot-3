package com.iroshnk.nftraffle.service.impl;


import com.iroshnk.nftraffle.entity.*;
import com.iroshnk.nftraffle.repository.EntitlementHasRoleRepository;
import com.iroshnk.nftraffle.repository.EntitlementRepository;
import com.iroshnk.nftraffle.repository.GroupHasEntitlementRepository;
import com.iroshnk.nftraffle.repository.UserHasGroupRepository;
import com.iroshnk.nftraffle.service.EntitlementService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class EntitlementServiceImpl implements EntitlementService {

    private final UserHasGroupRepository userHasGroupRepository;
    private final GroupHasEntitlementRepository groupHasEntitlementRepository;
    private final EntitlementHasRoleRepository entitlementHasRoleRepository;
    private final EntitlementRepository entitlementRepository;

    public EntitlementServiceImpl(UserHasGroupRepository userHasGroupRepository,
                                  GroupHasEntitlementRepository groupHasEntitlementRepository,
                                  EntitlementHasRoleRepository entitlementHasRoleRepository,
                                  EntitlementRepository entitlementRepository) {
        this.userHasGroupRepository = userHasGroupRepository;
        this.groupHasEntitlementRepository = groupHasEntitlementRepository;
        this.entitlementHasRoleRepository = entitlementHasRoleRepository;
        this.entitlementRepository = entitlementRepository;
    }

    @Override
    public Set<String> getRolesByUser(Long userId) {
        List<Long> groupIds = userHasGroupRepository.findGroupIdsByUserId(userId);
        List<Long> entitlementIds= groupHasEntitlementRepository.findEntitlementIdsByGroupIds(groupIds);

        return entitlementHasRoleRepository.findRoleNamesByEntitlementIds(entitlementIds);
    }

    @Override
    public List<Entitlement> getEntitlements() {
        return entitlementRepository.findAll();
    }

    @Override
    public List<Entitlement> getEntitlementsByIds(List<Long> entitlementIds) {
        return entitlementRepository.findEntitlementsByEntitlementIdIn(entitlementIds);
    }
}
