package tone.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tone.analyzer.domain.entity.Conversation;
import tone.analyzer.domain.entity.Message;

/**
 * Created by user on 4/11/2017.
 */
public interface MessageRepository extends MongoRepository<Message, String> {
}
