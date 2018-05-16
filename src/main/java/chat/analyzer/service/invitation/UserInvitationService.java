package chat.analyzer.service.invitation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import chat.analyzer.domain.entity.EmailInvitation;
import chat.analyzer.domain.model.UserEmailInvitationNotification;
import chat.analyzer.domain.repository.EmailInvitationRepository;
import chat.analyzer.utility.CommonUtility;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;

import java.util.UUID;

@Service
public class UserInvitationService {

  private static final Logger LOG = LoggerFactory.getLogger(UserInvitationService.class);
  public static final String EMAIL_CONFIRMATION_RESOURCE = "/confirmationEmail";
  public static final String RECEIVER_UNKNOWN = "";

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
    String url = commonUtility.findBaseUrl(request) + EMAIL_CONFIRMATION_RESOURCE;

    UserEmailInvitationNotification userEmailInvitationNotification =
        commonUtility.createEmailTemplate(sender, invitedUser, invitedText, email, url, token);
    rabbitTemplate.convertAndSend(rabbitmqQueue, userEmailInvitationNotification);
  }

  public void inviteAnonymousUserByUrlLink(String sender, String token)
      throws MalformedURLException {

    EmailInvitation emailInvitation = new EmailInvitation(sender, RECEIVER_UNKNOWN, token);
    emailInvitationRepository.save(emailInvitation);
  }
}
