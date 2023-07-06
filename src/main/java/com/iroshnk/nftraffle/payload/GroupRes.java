package com.iroshnk.nftraffle.payload;

import com.iroshnk.nftraffle.entity.Entitlement;
import com.iroshnk.nftraffle.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupRes {

    private Group group;
    private List<Entitlement> entitlements;

}
