package tone.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.Review;

import java.util.List;

/** Created by mozammal on 4/25/17. */
public interface ReviewRepository extends MongoRepository<Review, String> {

  public List<Review> findByUser(String user);
}
