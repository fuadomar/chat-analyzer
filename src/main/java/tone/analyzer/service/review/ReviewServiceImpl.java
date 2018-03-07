package tone.analyzer.service.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.entity.Review;
import tone.analyzer.domain.repository.ReviewRepository;

/**
 * Created by mozammal on 4/25/17.
 */
@Service
public class ReviewServiceImpl implements ReviewService {

  @Autowired
  private ReviewRepository reviewRepository;

  @Override
  public void saveReview(Review review) {
    reviewRepository.save(review);
  }
}
