package com.iroshnk.nftraffle.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginReq {

    @SchemaProperty
    @NotBlank(message = "user name required")
    private String username;
    @SchemaProperty
    @NotBlank(message = "password required")
    private String password;
}
