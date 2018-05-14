package tone.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.entity.Token;

/** Created by user on 1/8/2018. */
public interface EmailInvitationRepository extends MongoRepository<EmailInvitation, String> {

  EmailInvitation findByToken(String token);

  EmailInvitation findByTokenAndSenderAndReceiver(String token, String sender, String receiver);
}
