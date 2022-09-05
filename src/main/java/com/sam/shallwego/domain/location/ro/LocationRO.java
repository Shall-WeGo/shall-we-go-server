package com.sam.shallwego.domain.location.ro;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationRO {

    @JsonProperty("location_name")
    private final String locationName;

    @JsonProperty("avg_rate")
    private final double rate;

}
