package tone.analyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.entity.User;
import tone.analyzer.domain.repository.UserRepository;

import java.util.List;

/** Created by mozammal on 4/20/17. */
@Service
public class AdminService {

  @Autowired private UserRepository userRepository;

  public List<User> fetchAllUsers() {
    return userRepository.findAll();
  }
}
