package com.iroshnk.nftraffle.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table
public class GroupHasEntitlement {

    @EmbeddedId
    private GroupHasEntitlementId groupHasEntitlementId = new GroupHasEntitlementId();

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @MapsId("entitlementId")
    @JoinColumn(name = "entitlement_id")
    private Entitlement entitlement;

    public GroupHasEntitlement(Group group, Entitlement entitlement) {
        this.group = group;
        this.entitlement = entitlement;
    }
}
