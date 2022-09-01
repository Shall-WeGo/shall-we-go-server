package com.sam.shallwego.domain.savelocation.entity;

import com.sam.shallwego.domain.location.entity.Location;
import com.sam.shallwego.domain.embedded.MemberId;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@Table(name = "save_location")
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveLocation {

    @EmbeddedId
    private MemberId memberId;

    @ManyToOne
    private Location location;

}
