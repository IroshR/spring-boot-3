package com.iroshnk.nftraffle.service.impl;

import com.iroshnk.nftraffle.entity.Entitlement;
import com.iroshnk.nftraffle.entity.Group;
import com.iroshnk.nftraffle.entity.GroupHasEntitlement;
import com.iroshnk.nftraffle.payload.GroupReq;
import com.iroshnk.nftraffle.payload.GroupRes;
import com.iroshnk.nftraffle.repository.GroupHasEntitlementRepository;
import com.iroshnk.nftraffle.repository.GroupRepository;
import com.iroshnk.nftraffle.service.EntitlementService;
import com.iroshnk.nftraffle.service.GroupService;
import com.iroshnk.nftraffle.util.ResponseDetails;
import com.iroshnk.nftraffle.util.exception.DataNotAcceptableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupHasEntitlementRepository groupHasEntitlementRepository;
    private final EntitlementService entitlementService;

    public GroupServiceImpl(GroupRepository groupRepository,
                            GroupHasEntitlementRepository groupHasEntitlementRepository,
                            EntitlementService entitlementService) {
        this.groupRepository = groupRepository;
        this.groupHasEntitlementRepository = groupHasEntitlementRepository;
        this.entitlementService = entitlementService;
    }

    @Transactional
    @Override
    public GroupRes createGroup(GroupReq groupReq) {
        Optional<Group> optionalGroup = groupRepository.findByGroupName(groupReq.getGroupName());
        if (optionalGroup.isPresent()) {
            throw new DataNotAcceptableException(ResponseDetails.E1007);
        }
        var group = new Group(groupReq.getGroupName());
        var savedGroup = groupRepository.save(group);

        List<Entitlement> entitlements = saveGroupHasEntitlement(groupReq, savedGroup);

        return new GroupRes(savedGroup, entitlements);
    }

    private List<Entitlement> saveGroupHasEntitlement(GroupReq groupReq, Group savedGroup) {
        List<Entitlement> entitlementsByIds = entitlementService.getEntitlementsByIds(groupReq.getEntitlementIds());

        List<GroupHasEntitlement> groupHasEntitlements = entitlementsByIds.stream()
                .parallel()
                .map(entitlement -> new GroupHasEntitlement(savedGroup, entitlement))
                .collect(Collectors.toList());

        List<GroupHasEntitlement> savedGroupHasEntitlements = groupHasEntitlementRepository.saveAll(groupHasEntitlements);

        return savedGroupHasEntitlements.stream()
                .map(GroupHasEntitlement::getEntitlement)
                .collect(Collectors.toList());
    }

    @Override
    public GroupRes getGroupById(Long groupId) {
        List<GroupHasEntitlement> groupHasEntitlementList = groupHasEntitlementRepository.getGroupHasEntitlementByGroupGroupId(groupId);
        Set<Entitlement> entitlements = groupHasEntitlementList.stream()
                .map(groupHasEntitlement ->
                        new Entitlement(
                                groupHasEntitlement.getEntitlement().getEntitlementId(),
                                groupHasEntitlement.getEntitlement().getEntitlementName())
                )
                .collect(Collectors.toSet());
        return new GroupRes(groupHasEntitlementList.get(0).getGroup(), new ArrayList<>(entitlements));
    }

    @Transactional
    @Override
    public GroupRes updateGroup(GroupReq groupReq) {
        Optional<Group> optionalGroup = groupRepository.findById(groupReq.getGroupId());
        if (optionalGroup.isEmpty()) {
            throw new DataNotAcceptableException(ResponseDetails.E1010);
        }
        var group = new Group(groupReq.getGroupId(), groupReq.getGroupName());
        var savedGroup = groupRepository.save(group);
        groupHasEntitlementRepository.deleteByGroupGroupId(groupReq.getGroupId());
        List<Entitlement> entitlements = saveGroupHasEntitlement(groupReq, savedGroup);
        return new GroupRes(savedGroup, entitlements);
    }
}
