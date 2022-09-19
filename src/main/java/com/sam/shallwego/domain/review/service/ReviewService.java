package com.sam.shallwego.domain.review.service;

import com.sam.shallwego.domain.embedded.ReviewId;
import com.sam.shallwego.domain.location.repository.LocationRepository;
import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.member.service.MemberService;
import com.sam.shallwego.domain.review.dto.ReviewDto;
import com.sam.shallwego.domain.review.entity.Review;
import com.sam.shallwego.domain.review.repository.HighRateReview;
import com.sam.shallwego.domain.review.repository.ReviewRepository;
import com.sam.shallwego.domain.review.ro.ReviewRO;
import com.sam.shallwego.domain.savelocation.service.LocationService;
import com.sam.shallwego.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.atomic.AtomicReference;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final JwtUtil jwtUtil;
    private final MemberService memberService;
    private final LocationService locationService;
    private final ReviewRepository reviewRepository;
    private final LocationRepository locationRepository;

    public Mono<ReviewRO> writeReview(final String token,
                                      final ReviewDto reviewDto) {
        AtomicReference<Member> writer = new AtomicReference<>();
        String username = jwtUtil.extractUsernameFromToken(token, "access");
        return locationService.findLocationOrElseByAddress(reviewDto.getLocation())
                .flatMap(location -> {
                        if (location.getId() == null) {
                            return Mono.fromCallable(() -> locationRepository.save(location))
                                    .subscribeOn(Schedulers.boundedElastic());
                        }

                        return Mono.just(location);
                })
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(location -> memberService.memberMonoByUsername(username)
                        .doOnNext(member -> {
                            writer.set(member);
                            ReviewId reviewId = new ReviewId(member, location);
                            Review review = Review.builder()
                                    .reviewId(reviewId)
                                    .content(reviewDto.getReviewMessage())
                                    .rate(reviewDto.getRate())
                                    .build();
                            Mono.fromCallable(() -> reviewRepository.save(review))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .subscribe();
                        }).publishOn(Schedulers.boundedElastic())
                        .subscribe())
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(new ReviewRO(writer.get(), reviewDto));
    }

    @Transactional(readOnly = true)
    public Flux<ReviewRO> findAllReview(final String address) {
        return locationService.findLocationByAddress(address)
                .flatMap(location -> Mono.fromCallable(() -> reviewRepository
                        .findAllByReviewIdLocation(location))
                        .subscribeOn(Schedulers.boundedElastic()))
                .flatMapMany(Flux::fromIterable)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(ReviewRO::new)
                .sequential();
    }

    public Mono<Void> deleteReview(final String token, final String address) {
        String username = jwtUtil.extractUsernameFromToken(token, "access");
        return memberService.memberMonoByUsername(username)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(member -> locationService.findLocationByAddress(address)
                        .publishOn(Schedulers.boundedElastic())
                        .doOnSuccess(location -> {
                            ReviewId reviewId = new ReviewId(member, location);
                            reviewRepository.deleteById(reviewId);
                        }).subscribe()).subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Transactional(readOnly = true)
    public Flux<HighRateReview> findAllByHighRate() {
        return Mono.fromCallable(reviewRepository::findAllByAvgRate)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .parallel()
                .runOn(Schedulers.parallel()).sequential();
    }

}
