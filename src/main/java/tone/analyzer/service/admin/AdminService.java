package tone.analyzer.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.entity.UserAccount;
import tone.analyzer.domain.repository.UserAccountRepository;

import java.util.List;

/** Created by mozammal on 4/20/17. */
@Service
public class AdminService {

  @Autowired private UserAccountRepository userRepository;

  public List<UserAccount> fetchAllUsers() {
    return userRepository.findAll();
  }
}
