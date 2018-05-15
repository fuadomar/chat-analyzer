package chat.analyzer.auth.service;

import chat.analyzer.domain.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/** Created by user on 3/18/2018. */
@Service
public class AnonymousSecurityServiceImpl implements SecurityService {

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private UserDetailsService userDetailsService;

  @Autowired private UserAccountRepository userAccountRepository;

  private static final Logger LOGGER = LoggerFactory.getLogger(AnonymousSecurityServiceImpl.class);

  @Override
  public String findLoggedInUsername() {
    return null;
  }

  @Override
  public void autoLogin(
      String username, String password, HttpServletRequest request, HttpServletResponse response) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ANONYMOUS_CHAT"));

    // generate session if one doesn't exist
    request.getSession();
    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetails(request));
    // SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    Authentication authenticatedUser =
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

    if (authenticatedUser.isAuthenticated()) {
      SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      LOGGER.info(String.format("Auto login %s successfully!", username));
    }
  }
}
