package tone.analyzer.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring4.SpringTemplateEngine;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.model.NewUserInvitationNotification;
import tone.analyzer.domain.repository.EmailInvitationRepository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

import org.thymeleaf.context.Context;


/** Created by Dell on 1/15/2018. */
@Service
public class MailService {

  private static final Logger log = LoggerFactory.getLogger(MailService.class);

  @Autowired private JavaMailSender mailSender;

  @Autowired private EmailInvitationRepository emailInvitationRepository;


  @Autowired
  private SpringTemplateEngine templateEngine;

  public void sendMail(NewUserInvitationNotification userInvitationNotification) throws MessagingException {

    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
            StandardCharsets.UTF_8.name());


    Context context = new Context();
    context.setVariable("name", userInvitationNotification.getReceiver());
    context.setVariable("url", userInvitationNotification.getUrl());
    context.setVariable("sender", userInvitationNotification.getSender());
    String html = templateEngine.process("email-invitation", context);
    log.info("email content: {}", html);

    helper.setTo(userInvitationNotification.getReceiver());
    helper.setText(html, true);
    helper.setSubject(userInvitationNotification.getSubject());
    helper.setFrom("<mozammaltomal.1001@mail.com>");
    mailSender.send(message);

    EmailInvitation emailInvitation =
        new EmailInvitation(
            userInvitationNotification.getSender(),
            userInvitationNotification.getReceiver(),
            userInvitationNotification.getToken());
    emailInvitationRepository.save(emailInvitation);
  }
}
