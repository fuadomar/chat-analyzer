package tone.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tone.analyzer.domain.entity.Conversation;

import java.util.List;

/** Created by user on 4/11/2017. */
public interface ConversationRepository extends MongoRepository<Conversation, String> {

  @Query(
          value =
                  "{ $or: [ { $and: [ {'sender' : ?0}, {'receiver' : ?1}  ] },  { $and: [ {'sender' : ?1}, {'receiver' : ?0}  ] } ]}"
  )
  Conversation findConversationBySenderAndRecipient(String sender, String recipient);



  List<Conversation> findBySender(String sender);
}
