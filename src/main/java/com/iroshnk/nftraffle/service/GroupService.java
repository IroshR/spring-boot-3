package com.iroshnk.nftraffle.service;

import com.iroshnk.nftraffle.payload.GroupReq;
import com.iroshnk.nftraffle.payload.GroupRes;

public interface GroupService {

    GroupRes createGroup(GroupReq groupReq);

    GroupRes getGroupById(Long groupId);

    GroupRes updateGroup(GroupReq groupReq);
}
