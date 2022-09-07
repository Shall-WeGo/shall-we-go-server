package com.sam.shallwego.domain.review.entity;

import com.sam.shallwego.domain.embedded.ReviewId;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@Table(name = "review")
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @EmbeddedId
    private ReviewId reviewId;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String content;

    private Short rate;

}
