package com.sam.shallwego.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor
public class ReissueDto {

    @JsonProperty("refresh_token")
    private String refreshToken;
}
