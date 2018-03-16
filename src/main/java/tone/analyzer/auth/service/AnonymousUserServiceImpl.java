package tone.analyzer.auth.service;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tone.analyzer.auth.service.UserService;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.Role;
import tone.analyzer.domain.repository.AccountRepository;

@Service
public class AnonymousUserServiceImpl implements UserService {

  @Autowired
  private AccountRepository userRepository;

  @Override
  public Account save(Account account) {

    account.setEnabled(true);
    account.setRole(Arrays.asList(new Role("ROLE_ANONYMOUS"),
        new Role("ROLE_USER")));
    userRepository.save(account);
    return account;
  }

  @Override
  public Account findByName(String username) {
    return null;
  }

  @Override
  public void addBudyyToUser(Account emailInvitationSenser, Account emailInvitationEeceiver) {

  }
}
