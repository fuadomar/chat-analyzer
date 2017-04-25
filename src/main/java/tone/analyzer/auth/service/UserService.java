package tone.analyzer.auth.service;

import tone.analyzer.domain.entity.Account;

/** Created by mozammal on 4/18/17. */
public interface UserService {

  void save(Account user);

  Account findByName(String username);

}
