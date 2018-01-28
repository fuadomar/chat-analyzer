package tone.analyzer.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.repository.AccountRepository;

import java.util.List;

/** Created by mozammal on 4/20/17. */
@Service
public class AdminService {

  @Autowired private AccountRepository userRepository;

  public List<Account> fetchAllUsers() {
    return userRepository.findAll();
  }
}
