package com.sam.shallwego.domain.member.ro;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignRO {

    @JsonProperty("user_id")
    private final long id;

}
