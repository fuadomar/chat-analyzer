package tone.analyzer.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring4.SpringTemplateEngine;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.model.UserEmailInvitationNotification;
import tone.analyzer.domain.repository.EmailInvitationRepository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

import org.thymeleaf.context.Context;

/**
 * Created by Dell on 1/15/2018.
 */
@Service
public class MailService {

  private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

  @Value("${mail.from}")
  private String mailFrom;

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private EmailInvitationRepository emailInvitationRepository;

  @Autowired
  private SpringTemplateEngine templateEngine;

  public void sendMail(UserEmailInvitationNotification userInvitationNotification)
      throws MessagingException {

    try {

      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper =
          new MimeMessageHelper(
              message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
              StandardCharsets.UTF_8.name());

      Context context = new Context();
      context.setVariables(userInvitationNotification.getModel());
      String html = templateEngine.process("emailInvitation", context);
      LOG.info("email content: {}", html);

      helper.setTo((String) userInvitationNotification.getModel().get("receiver"));
      helper.setText(html, true);
      helper.setSubject(userInvitationNotification.getSubject());
      helper.setFrom(mailFrom);
   //   mailSender.send(message);

      EmailInvitation emailInvitation =
          new EmailInvitation(
              (String) userInvitationNotification.getModel().get("sender"),
              (String) userInvitationNotification.getModel().get("receiver"),
              userInvitationNotification.getToken());
      emailInvitationRepository.save(emailInvitation);
    } catch (Exception exception) {
      LOG.info("exception sending email {}", exception.getCause());
    }
  }
}
