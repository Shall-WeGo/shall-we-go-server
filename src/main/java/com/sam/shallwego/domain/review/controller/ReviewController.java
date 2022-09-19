package com.sam.shallwego.domain.review.controller;

import com.sam.shallwego.domain.review.dto.ReviewDto;
import com.sam.shallwego.domain.review.repository.HighRateReview;
import com.sam.shallwego.domain.review.ro.ReviewRO;
import com.sam.shallwego.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;

@Slf4j
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

    @GetMapping(value = "{address}", produces = "application/stream+json")
    public Flux<ReviewRO> findReviewByAddress(@PathVariable("address") String address) {
        return reviewService.findAllReview(address);
    }

    @DeleteMapping
    public Mono<Void> deleteReview(Mono<Authentication> authentication,
                                   @RequestParam("address") String address) {
        log.warn("request");
        return authentication
                .map(auth -> auth.getCredentials().toString())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(token -> reviewService.deleteReview(token, address))
                .log("response");
    }

    @GetMapping(produces = "application/stream+json")
    public Flux<HighRateReview> findAllHighReview() {
        return reviewService.findAllByHighRate();
    }
}
