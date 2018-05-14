package tone.analyzer.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.model.UserEmailInvitationNotification;
import tone.analyzer.domain.repository.EmailInvitationRepository;
import tone.analyzer.service.invitation.UserInvitationService;
import tone.analyzer.utility.CommonUtility;

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
