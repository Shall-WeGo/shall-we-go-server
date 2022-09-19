package com.sam.shallwego.domain.review.repository;

import com.sam.shallwego.domain.embedded.ReviewId;
import com.sam.shallwego.domain.location.entity.Location;
import com.sam.shallwego.domain.review.entity.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends CrudRepository<Review, ReviewId> {

    List<Review> findAllByReviewIdLocation(Location reviewIdLocation);

    @Query("select new com.sam.shallwego.domain.review.repository.HighRateReview(r.reviewId.location.address, avg(r.rate)) " +
            "from Review r group by r.reviewId.location having avg(r.rate) >= 4.0")
    List<HighRateReview> findAllByAvgRate();

}
