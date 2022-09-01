package com.sam.shallwego.domain.embedded;

import com.sam.shallwego.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Getter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor @NoArgsConstructor
public class MemberId implements Serializable {

    @ManyToOne
    private Member member;

}
