package com.iroshnk.nftraffle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table
public class Entitlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entitlementId;
    private String entitlementName;
    @JsonIgnore
    @OneToMany(mappedBy = "entitlement")
    private Set<GroupHasEntitlement> groupHasEntitlements;
    @JsonIgnore
    @OneToMany(mappedBy = "entitlement")
    private Set<EntitlementHasRole> entitlementHasRoles;

    public Entitlement(Long entitlementId, String entitlementName) {
        this.entitlementId = entitlementId;
        this.entitlementName = entitlementName;
    }

    public Entitlement(String entitlementName) {
        this.entitlementName = entitlementName;
    }
}
