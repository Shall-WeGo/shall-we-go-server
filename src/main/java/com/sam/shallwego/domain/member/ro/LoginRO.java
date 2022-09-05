package com.sam.shallwego.domain.member.ro;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRO {

    @JsonProperty("access_token")
    private final String accessToken;

    @JsonProperty("refresh_token")
    private final String refreshToken;
}
