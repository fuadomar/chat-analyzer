package tone.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tone.analyzer.domain.entity.FlaggedMessage;
import tone.analyzer.domain.entity.Message;

/** Created by mozammal on 4/26/17. */
public interface FlaggedMessageRepository extends MongoRepository<FlaggedMessage, String> {}
