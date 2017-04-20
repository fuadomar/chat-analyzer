package tone.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tone.analyzer.domain.entity.User;

/** Created by mozammal on 4/18/17. */
public interface UserRepository extends MongoRepository<User, String> {

  public User findByName(String username);
}
