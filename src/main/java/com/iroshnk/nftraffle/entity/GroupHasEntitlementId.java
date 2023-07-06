package com.iroshnk.nftraffle.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
@AllArgsConstructor
public class GroupHasEntitlementId implements Serializable {

    private Long groupId;
    private Long entitlementId;

}
