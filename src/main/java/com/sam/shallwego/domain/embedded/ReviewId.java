package com.sam.shallwego.domain.embedded;

import com.sam.shallwego.domain.location.entity.Location;
import com.sam.shallwego.domain.member.entity.Member;
import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Getter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewId implements Serializable {

    @ManyToOne
    private Member member;

    @ManyToOne
    private Location location;

}
