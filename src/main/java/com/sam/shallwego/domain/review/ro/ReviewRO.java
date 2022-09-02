package com.sam.shallwego.domain.review.ro;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewRO {

    @JsonProperty("review_message")
    private final String ReviewMessage;

    private final short rate;

}
