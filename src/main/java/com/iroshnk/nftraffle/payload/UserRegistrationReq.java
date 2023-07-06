package com.iroshnk.nftraffle.payload;

import com.iroshnk.nftraffle.entity.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationReq {

//    private final String PASSWORD_REGX = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$.@$!%*?&])[A-Za-z\\d$@$!%*?&].{9,}";

    private Long userId;
    @Email(message = "Email is required")
    private String email;
    @NotBlank(message = "password is required")
//    @Pattern(regexp = PASSWORD_REGX, message = "Invalid Password Pattern")
    private String password;
    private String firstName;
    private String lastName;
    private String primaryPhone;
    private String secondaryPhone;
    private String businessTitle;
    private List<Long> userGroups;
    private UserStatus userStatus;

}
