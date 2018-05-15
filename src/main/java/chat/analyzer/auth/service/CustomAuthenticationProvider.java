package chat.analyzer.auth.service;

import chat.analyzer.domain.entity.Role;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

  @Autowired private UserAccountRepository userAccountRepository;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    String userName = authentication.getName();
    String password =
        authentication.getCredentials() != null
            ? authentication.getCredentials().toString().trim()
            : null;
    UserAccount user = userAccountRepository.findByName(userName);
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    if (user == null) {
      throw new UsernameNotFoundException("UserAccount not found");
    }
    boolean matchesPassword = false;

    if (password != null)
      matchesPassword = new BCryptPasswordEncoder().matches(password, user.getPassword());
    if (authentication.getCredentials() != null && !matchesPassword) {
      return null;
    }

    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    for (Role role : user.getRole()) {
      grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
    }

    return new UsernamePasswordAuthenticationToken(userName, password, grantedAuthorities);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
