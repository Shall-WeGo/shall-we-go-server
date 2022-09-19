package com.sam.shallwego.domain.review.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HighRateReview {

    private final String address;
    private final Double rate;

}
