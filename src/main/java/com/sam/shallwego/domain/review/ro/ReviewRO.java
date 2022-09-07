package com.sam.shallwego.domain.review.ro;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sam.shallwego.domain.review.dto.ReviewDto;
import lombok.Getter;

@Getter
public class ReviewRO {

    @JsonProperty("review_message")
    private final String reviewMessage;

    private final short rate;

    public ReviewRO(ReviewDto reviewDto) {
        this.reviewMessage = reviewDto.getReviewMessage();
        this.rate = (reviewDto.getRate() == null)
                ? 0
                : reviewDto.getRate();
    }
}
