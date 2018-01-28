package tone.analyzer.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.entity.Role;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.repository.AccountRepository;

import java.util.Arrays;

/** Created by mozammal on 4/18/17. */
@Service
public class UserServiceImpl implements UserService {

  @Autowired private AccountRepository userRepository;

  @Override
  public Account save(Account user) {

    user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
    user.setEnabled(true);
    Role role = new Role("ROLE_USER");
    user.setRole(Arrays.asList(role));
    userRepository.save(user);
    return user;
  }

  @Override
  public Account findByName(String username) {
    return userRepository.findByName(username);
  }

  @Override
  public void addBudyyToUser(Account emailInvitationSender, Account emailInvitationReceiver) {

    userRepository.save(emailInvitationSender);
    userRepository.save(emailInvitationReceiver);
  }
}
