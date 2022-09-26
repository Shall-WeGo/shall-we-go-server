package com.sam.shallwego.domain.review.ro;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.review.dto.ReviewDto;
import com.sam.shallwego.domain.review.entity.Review;
import lombok.Getter;

@Getter
public class ReviewRO {

    @JsonProperty("review_message")
    private final String reviewMessage;

    private final short rate;

    private final String writer;

    public ReviewRO(Member member, ReviewDto reviewDto) {
        this.reviewMessage = reviewDto.getReviewMessage();
        this.rate = (reviewDto.getRate() == null) ? 0 : reviewDto.getRate();
        this.writer = member.getUsername();
    }

    public ReviewRO(Review review) {
        this.reviewMessage = review.getContent();
        this.rate = (review.getRate() == null) ? 0 : review.getRate();
        this.writer = review.getReviewId().getMember().getUsername();
    }
}
