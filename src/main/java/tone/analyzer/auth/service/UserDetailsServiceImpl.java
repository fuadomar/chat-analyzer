package tone.analyzer.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.Role;
import tone.analyzer.domain.repository.AccountRepository;

import java.util.ArrayList;
import java.util.List;

/** Created by mozammal on 4/18/17. */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired private AccountRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Account user = userRepository.findByName(username);
    if (user == null) throw new UsernameNotFoundException("Account not found");

    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    for (Role role : user.getRole()) {
      grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
    }

    return new org.springframework.security.core.userdetails.User(
        user.getName(), user.getPassword(), grantedAuthorities);
  }
}
