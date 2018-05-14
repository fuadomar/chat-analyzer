package tone.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tone.analyzer.domain.entity.UserAccount;

import java.util.List;

/** Created by mozammal on 4/18/17. */
public interface UserAccountRepository extends MongoRepository<UserAccount, String> {

  UserAccount findByName(String username);

  @Query(value = "{}", fields = "{'password': 0}, {'role': 0}")
  List<UserAccount> findAll();
}
