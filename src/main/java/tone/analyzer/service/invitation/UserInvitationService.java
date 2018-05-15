package tone.analyzer.service.invitation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.model.UserEmailInvitationNotification;
import tone.analyzer.domain.repository.EmailInvitationRepository;
import tone.analyzer.utility.CommonUtility;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;

import java.util.UUID;

@Service
public class UserInvitationService {

  private static final Logger LOG = LoggerFactory.getLogger(UserInvitationService.class);

  @Autowired private RabbitTemplate rabbitTemplate;

  @Autowired private String rabbitmqQueue;

  @Autowired private CommonUtility commonUtility;

  @Autowired EmailInvitationRepository emailInvitationRepository;

  public void inviteUserByEmail(
      String sender,
      String email,
      String invitedText,
      String invitedUser,
      HttpServletRequest request)
      throws MalformedURLException {

    String token = UUID.randomUUID().toString();
    String url = commonUtility.findBaseUrl(request) + "/confirmationEmail";

    UserEmailInvitationNotification userEmailInvitationNotification =
        commonUtility.createEmailTemplate(sender, invitedUser, invitedText, email, url, token);
    rabbitTemplate.convertAndSend(rabbitmqQueue, userEmailInvitationNotification);
  }

  public String inviteAnonymousUserByUrlLink(HttpServletRequest request)
      throws MalformedURLException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String sender = commonUtility.findPrincipalNameFromAuthentication(auth);
    String token = UUID.randomUUID().toString();
    String url = commonUtility.findBaseUrl(request) + "/chat/anonymous";
    String confirmationUrl = url + "?token=" + token;

    LOG.info(confirmationUrl);
    EmailInvitation emailInvitation = new EmailInvitation(sender, "", token);
    emailInvitationRepository.save(emailInvitation);
    return confirmationUrl;
  }
}