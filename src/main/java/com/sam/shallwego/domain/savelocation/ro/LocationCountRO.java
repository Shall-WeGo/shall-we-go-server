package com.sam.shallwego.domain.savelocation.ro;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationCountRO {

    @JsonProperty("save_count")
    private final long saveCount;

}
