package tone.analyzer.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.entity.User;
import tone.analyzer.domain.repository.UserRepository;

import java.util.HashSet;

/** Created by mozammal on 4/18/17. */
@Service
public class UserServiceImpl implements UserService {
  @Autowired private UserRepository userRepository;

  @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public void save(User user) {
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    userRepository.save(user);
  }

  @Override
  public User findByEmail(String username) {
    return userRepository.findByEmail(username);
  }
}
