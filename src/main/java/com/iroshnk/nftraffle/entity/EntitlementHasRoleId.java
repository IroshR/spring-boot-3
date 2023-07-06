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
public class EntitlementHasRoleId implements Serializable {

    private Long entitlementId;
    private Long roleId;
}
