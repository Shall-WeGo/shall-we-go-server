package com.sam.shallwego.domain.review.repository;

import com.sam.shallwego.domain.embedded.ReviewId;
import com.sam.shallwego.domain.location.entity.Location;
import com.sam.shallwego.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends CrudRepository<Review, ReviewId> {

    Page<Review> findAllByReviewIdLocation(
            Location reviewIdLocation, Pageable pageable
    );

    @Query("select new java.lang.Double(avg(r.rate)) " +
            "from Review r group by r.reviewId.location having avg(r.rate) >= 4.0")
    Double findAllByAvgRate();

}
