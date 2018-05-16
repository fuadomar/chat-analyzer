package chat.analyzer.auth.service;

import chat.analyzer.domain.entity.Role;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/** Created by mozammal on 4/18/17. */
@Service
public class UserServiceImpl implements UserService {

  @Autowired private UserAccountRepository userRepository;

  @Override
  public UserAccount save(UserAccount user) {

    user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
    user.setEnabled(true);
    Role role = new Role("ROLE_USER");
    user.setRole(Arrays.asList(role));
    userRepository.save(user);
    return user;
  }

  @Override
  public UserAccount findByName(String username) {
    return userRepository.findByName(username);
  }

  @Override
  public void addBuddyToUserBuddyList(
      UserAccount emailInvitationSender, UserAccount emailInvitationReceiver) {

    userRepository.save(emailInvitationSender);
    userRepository.save(emailInvitationReceiver);
  }
}
