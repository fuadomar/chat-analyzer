package tone.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tone.analyzer.domain.entity.User;
import tone.analyzer.domain.entity.UserName;

import java.util.List;

/** Created by mozammal on 4/18/17. */
public interface UserRepository extends MongoRepository<User, String> {

  public User findByName(String username);

  @Query(value = "{}", fields = "{'password': 0}, {'role': 0}")
  List<User> findAll();
}
