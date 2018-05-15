package chat.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import chat.analyzer.domain.entity.Token;

/** Created by mozammal on 5/4/17. */
public interface TokenRepository extends MongoRepository<Token, String> {

  Token findBySeries(String series);

  Token findByUsername(String username);
}
