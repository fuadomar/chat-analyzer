package chat.analyzer.auth.service;

import chat.analyzer.dao.UserAccountDao;
import chat.analyzer.domain.entity.Role;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.repository.UserAccountRepository;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AnonymousUserServiceImpl implements UserService {


  @Autowired private UserAccountDao userAccountDao;

  @Override
  public UserAccount save(UserAccount userAccount) {
    userAccount.setPassword(new BCryptPasswordEncoder().encode(userAccount.getPassword()));
    userAccount.setEnabled(true);
    userAccount.setRole(Arrays.asList(new Role("ROLE_ANONYMOUS_CHAT"), new Role("ROLE_USER")));
    userAccountDao.save(userAccount);
    return userAccount;
  }

  @Override
  public UserAccount findByName(String username) {
    return userAccountDao.findByName(username);
  }

  @Override
  public void addBudyyToUser(
      UserAccount emailInvitationSender, UserAccount emailInvitationReceiver) {
    userAccountDao.save(emailInvitationSender);
    userAccountDao.save(emailInvitationReceiver);
  }
}
