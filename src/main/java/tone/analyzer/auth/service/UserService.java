package tone.analyzer.auth.service;

import tone.analyzer.domain.entity.Account;

/**
 * Created by mozammal on 4/18/17.
 */
public interface UserService {

  Account save(Account account);

  Account findByName(String username);

  public void addBudyyToUser(Account emailInvitationSenser, Account emailInvitationEeceiver);
}
