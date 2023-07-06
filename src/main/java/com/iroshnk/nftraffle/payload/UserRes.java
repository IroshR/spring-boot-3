package com.iroshnk.nftraffle.payload;

import com.iroshnk.nftraffle.entity.Group;
import com.iroshnk.nftraffle.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRes {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String primaryPhoneNumber;
    private String secondaryPhoneNumber;
    private String businessTitle;
    private List<Group> groups;
    private UserStatus userStatus;

}
