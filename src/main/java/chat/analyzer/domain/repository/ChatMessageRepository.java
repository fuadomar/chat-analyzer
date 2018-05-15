package chat.analyzer.domain.repository;

import chat.analyzer.domain.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/** Created by user on 4/11/2017. */
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

  List<ChatMessage> findAllByConversationId(String conversationId);

  @Query(
    value =
        "{ $or: [ { $and: [ {'sender' : ?0}, {'receiver' : ?1}  ] },  { $and: [ {'sender' : ?1}, {'receiver' : ?0}  ] } ]}"
  )
  List<ChatMessage> findMessagesBySenderAndReceiver(
      String sender, String recipient, org.springframework.data.domain.Sort sort);

  @Query(value = "{ $and: [ {'sender' : ?0}, {'receiver' : ?1}  ] }")
  List<ChatMessage> findMessagesReceivedFromSender(
      String sender, String recipient, org.springframework.data.domain.Sort sort);
}
