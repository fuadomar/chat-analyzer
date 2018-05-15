package chat.analyzer.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/** Created by mozammal on 4/18/17. */
@Service
public class SecurityServiceImpl implements SecurityService {

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private UserDetailsService userDetailsService;

  private static final Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);

  @Override
  public String findLoggedInUsername() {

    Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
    if (userDetails instanceof UserDetails) {
      return ((UserDetails) userDetails).getUsername();
    }

    return null;
  }

  @Override
  public void autoLogin(
      String username, String password, HttpServletRequest request, HttpServletResponse response) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(userDetails, password, authorities);

    // generate session if one doesn't exist
    request.getSession();
    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetails(request));
    Authentication authenticatedUser =
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

    if (authenticatedUser.isAuthenticated()) {
      SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      logger.info(String.format("Auto login %s successfully!", username));
    }
  }
}
