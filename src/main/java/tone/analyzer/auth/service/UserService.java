package tone.analyzer.auth.service;

import tone.analyzer.domain.entity.User;

/** Created by mozammal on 4/18/17. */
public interface UserService {
  void save(User user);

  User findByEmail(String username);
}
