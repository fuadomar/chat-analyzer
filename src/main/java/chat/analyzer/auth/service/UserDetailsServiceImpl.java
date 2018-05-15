package chat.analyzer.auth.service;

import chat.analyzer.dao.UserAccountDao;
import chat.analyzer.domain.entity.Role;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Created by mozammal on 4/18/17. */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired private UserAccountDao userAccountDao;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    UserAccount user = userAccountDao.findByName(username);
    if (user == null) {
      throw new UsernameNotFoundException("UserAccount not found");
    }

    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    for (Role role : user.getRole()) {
      grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
    }

    return new org.springframework.security.core.userdetails.User(
        user.getName(), user.getPassword(), grantedAuthorities);
  }
}
