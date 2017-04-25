package tone.analyzer.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.ChatMessage;
import tone.analyzer.domain.entity.Review;
import tone.analyzer.review.service.ReviewService;

/**
 * Created by mozammal on 4/25/17.
 */
@Component
public class ReviewGateway {

    @Autowired
    private ReviewService reviewService;

    public void saveReview(Review review) {
        reviewService.saveReview(review);
    }
}
