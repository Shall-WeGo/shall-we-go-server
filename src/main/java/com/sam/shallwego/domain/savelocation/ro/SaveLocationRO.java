package com.sam.shallwego.domain.savelocation.ro;

import com.sam.shallwego.domain.savelocation.entity.SaveLocation;
import lombok.Getter;

@Getter
public class SaveLocationRO {

    private final String location;

    public SaveLocationRO(SaveLocation saveLocation) {
        this.location = saveLocation.getLocation().getAddress();
    }
}
