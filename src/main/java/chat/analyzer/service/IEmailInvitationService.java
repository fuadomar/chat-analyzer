package chat.analyzer.service;

import chat.analyzer.domain.entity.EmailInvitation;

/** Created by user on 1/8/2018. */
public interface IEmailInvitationService {

  EmailInvitation findByToeknAndSenderAndReceiver(String token, String sender, String receiver);

  EmailInvitation findByToken(String token);
}
