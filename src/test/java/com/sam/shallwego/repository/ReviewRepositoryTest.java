package com.sam.shallwego.repository;

import com.sam.shallwego.domain.embedded.ReviewId;
import com.sam.shallwego.domain.location.entity.Location;
import com.sam.shallwego.domain.location.repository.LocationRepository;
import com.sam.shallwego.domain.member.entity.Member;
import com.sam.shallwego.domain.member.repository.MemberRepository;
import com.sam.shallwego.domain.review.entity.Review;
import com.sam.shallwego.domain.review.repository.HighRateReview;
import com.sam.shallwego.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.util.StopWatch;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member() {
        return memberRepository.save(new Member(null, "username", "1234"));
    }

    private Location location() {
        return locationRepository.save(new Location(null, "대소고"));
    }

    @DisplayName("장소로 모든 리뷰 조회 테스트")
    @Test
    void findAllByReviewIdLocationTest() {
        // given
        Member member = member();
        Location location = location();
        Review review = Review.builder()
                .reviewId(new ReviewId(member, location))
                .content("내용")
                .rate((short) 1)
                .build();
        reviewRepository.save(review);

        // when
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Review> reviewPage = reviewRepository.findAllByReviewIdLocation(location);
        stopWatch.stop();

        // then
        assertThat(reviewPage).isNotNull();
        assertThat(reviewPage).isNotEmpty();
        assertThat(reviewPage).hasSize(1);

        System.out.println(stopWatch.getTotalTimeMillis() + "ms");
    }
    
    @DisplayName("장소별 평점 평균 4.0 이상 테스트")
    @Test
    void findAllByAvgRate() {
        // given
        Member member = member();
        Location location = location();
        Review review = Review.builder()
                .reviewId(new ReviewId(member, location))
                .content("내용")
                .rate((short) 4)
                .build();
        reviewRepository.save(review);
    
        // when
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<HighRateReview> highRateReviewList = reviewRepository.findAllByAvgRate();
        stopWatch.stop();
        
        // then
        assertThat(highRateReviewList).isNotNull();
        assertThat(highRateReviewList).isNotEmpty();
        assertThat(highRateReviewList).hasSize(1);
        
        System.out.println(stopWatch.getTotalTimeMillis() + "ms");
    }
}
