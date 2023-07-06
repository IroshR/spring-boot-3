package com.iroshnk.nftraffle.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupReq {
    private Long groupId;
    private String groupName;
    private List<Long> entitlementIds;
}
