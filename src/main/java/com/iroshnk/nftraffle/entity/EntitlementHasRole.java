package com.iroshnk.nftraffle.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table
public class EntitlementHasRole {


    @EmbeddedId
    private EntitlementHasRoleId groupHasEntitlementId = new EntitlementHasRoleId();

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @MapsId("entitlementId")
    @JoinColumn(name = "entitlement_id")
    private Entitlement entitlement;
}
