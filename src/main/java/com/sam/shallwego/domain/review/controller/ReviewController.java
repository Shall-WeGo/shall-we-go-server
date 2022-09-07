package com.sam.shallwego.domain.review.controller;

import com.sam.shallwego.domain.review.dto.ReviewDto;
import com.sam.shallwego.domain.review.ro.ReviewRO;
import com.sam.shallwego.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Mono<ReviewRO> addReview(Mono<Authentication> authentication,
                                    @RequestBody @Valid ReviewDto reviewDto) {
        return authentication
                .map(auth -> auth.getCredentials().toString())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(token -> reviewService.writeReview(token, reviewDto));
    }

}
