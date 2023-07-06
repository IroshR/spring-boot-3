package com.iroshnk.nftraffle.payload;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class TokenRefreshRes {
    @NonNull
    private String accessToken;
    @NonNull
    private String refreshToken;
    private String tokenType = "Bearer";
}
