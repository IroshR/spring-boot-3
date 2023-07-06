package com.iroshnk.nftraffle.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordReq {

    private Long userId;
    private String oldPassword;
    private String newPassword;
}
