package com.sam.shallwego.domain.savelocation.ro;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sam.shallwego.domain.savelocation.entity.SaveLocation;
import lombok.Getter;

@Getter
public class SaveLocationRO {

    private final long id;

    private final String address;

    @JsonProperty("place_name")
    private final String placeName;

    @JsonProperty("coordinate_x")
    private double coordinateX;

    @JsonProperty("coordinate_y")
    private double coordinateY;

    public SaveLocationRO(SaveLocation saveLocation) {
        this.id = saveLocation.getId();
        this.address = saveLocation.getLocation().getAddress();
        this.placeName = saveLocation.getLocation().getPlace();
        this.coordinateX = saveLocation.getLocation().getCoordinateX();
        this.coordinateY = saveLocation.getLocation().getCoordinateY();
    }
}
