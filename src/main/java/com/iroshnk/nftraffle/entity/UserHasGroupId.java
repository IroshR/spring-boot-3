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
public class UserHasGroupId implements Serializable {

    private Long userId;
    private Long groupId;

}
