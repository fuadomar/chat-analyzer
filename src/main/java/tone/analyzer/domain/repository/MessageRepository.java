package tone.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tone.analyzer.domain.entity.Conversation;
import tone.analyzer.domain.entity.Message;

import java.util.List;

/** Created by user on 4/11/2017. */
public interface MessageRepository extends MongoRepository<Message, String> {

  List<Message> findAllByConversationId(String conversationId);

  @Query(
    value =
        "{ $or: [ { $and: [ {'sender' : ?0}, {'receiver' : ?1}  ] },  { $and: [ {'sender' : ?1}, {'receiver' : ?0}  ] } ]}"
  )
  List<Message> findMessagesBySenderAndReceiver(
      String sender, String recipient, org.springframework.data.domain.Sort sort);

  @Query(value = "{ $and: [ {'sender' : ?0}, {'receiver' : ?1}  ] }")
  List<Message> findReceivedMessagesByReceiverFromSender(
      String sender, String recipient, org.springframework.data.domain.Sort sort);
}
