package chat.analyzer.gateway;

import chat.analyzer.service.invitation.UserInvitationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import chat.analyzer.domain.repository.EmailInvitationRepository;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;

@Component
public class UserInvitationGateway {

  private static final Logger LOG = LoggerFactory.getLogger(UserInvitationGateway.class);

  @Autowired EmailInvitationRepository emailInvitationRepository;

  @Autowired private UserInvitationService userInvitationService;

  public String inviteUserByEmail(
      String sender,
      String email,
      String invitedText,
      String invitedUser,
      HttpServletRequest request)
      throws MalformedURLException {
    userInvitationService.inviteUserByEmail(sender, email, invitedText, invitedUser, request);
    return "Ok";
  }

  @RequestMapping(value = "/anonymousChatLink", method = RequestMethod.GET)
  public String inviteAnonymousUserByGeneratingLink(HttpServletRequest request)
      throws MalformedURLException {

    return userInvitationService.inviteAnonymousUserByUrlLink(request);
  }
}
