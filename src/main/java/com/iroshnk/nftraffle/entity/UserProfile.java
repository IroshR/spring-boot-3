package com.iroshnk.nftraffle.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String primaryPhone;
    private String secondaryPhone;
    private boolean isEmailConfirmed = false;
    private String businessTitle;
    private boolean needToChangePassword;
}
