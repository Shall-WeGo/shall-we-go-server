package com.sam.shallwego.domain.review.ro;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewListRO {

    private final List<ReviewRO> list;

    public ReviewListRO(List<ReviewRO> list) {
        this.list = list;
    }
}
