package com.sam.shallwego.domain.savelocation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor @NoArgsConstructor
public class LocationDto {

    @NotBlank
    @JsonProperty("address")
    private String location;

    @NotBlank
    @JsonProperty("place_name")
    private String place;

    @NotNull
    @JsonProperty("coordinate_x")
    private double coordinateX;

    @NotNull
    @JsonProperty("coordinate_y")
    private double coordinateY;

}
