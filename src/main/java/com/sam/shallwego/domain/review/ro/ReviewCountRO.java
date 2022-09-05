package com.sam.shallwego.domain.review.ro;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewCountRO {

    @JsonProperty("review_count")
    private final long reviewCount;

}
