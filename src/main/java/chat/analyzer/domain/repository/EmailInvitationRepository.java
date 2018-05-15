package chat.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import chat.analyzer.domain.entity.EmailInvitation;

/** Created by user on 1/8/2018. */
public interface EmailInvitationRepository extends MongoRepository<EmailInvitation, String> {

  EmailInvitation findByToken(String token);

  EmailInvitation findByTokenAndSenderAndReceiver(String token, String sender, String receiver);
}
