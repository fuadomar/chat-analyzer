package tone.analyzer.auth.service;

import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.EmailInvitation;

/** Created by user on 1/8/2018. */
public interface IEmailInvitationService {

  EmailInvitation findByToekn(String token);
}
