package com.sam.shallwego.domain.member.ro;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sam.shallwego.domain.member.entity.Member;
import lombok.Getter;

@Getter
public class SignRO {

    @JsonProperty("user_id")
    private final long id;

    public SignRO(Member member) {
        this.id = member.getId();
    }
}
