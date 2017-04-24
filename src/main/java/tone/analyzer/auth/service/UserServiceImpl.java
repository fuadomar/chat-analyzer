package tone.analyzer.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.entity.Role;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.repository.AccountRepository;

import java.util.ArrayList;
import java.util.List;

/** Created by mozammal on 4/18/17. */
@Service
public class UserServiceImpl implements UserService {
  @Autowired private AccountRepository userRepository;

  @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public void save(Account user) {
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    List<Role> roleList = new ArrayList<>();
    Role role = new Role("ROLE_USER");
    roleList.add(role);
    user.setRole(roleList);
    userRepository.save(user);
  }

  @Override
  public Account findByName(String username) {
    return userRepository.findByName(username);
  }
}
