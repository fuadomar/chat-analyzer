package tone.analyzer.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.model.NewUserInvitationNotification;
import tone.analyzer.domain.repository.EmailInvitationRepository;

/** Created by Dell on 1/15/2018. */
@Service
public class MailService {

  @Autowired private JavaMailSender mailSender;

  @Autowired private EmailInvitationRepository emailInvitationRepository;

  public void sendMail(NewUserInvitationNotification userInvitationNotification) {

    String url = userInvitationNotification.getUrl();
    String recipientAddress = userInvitationNotification.getReceiver();
    String confirmationUrl =
        "?token="
            + userInvitationNotification.getToken()
            + "&sender="
            + userInvitationNotification.getSender()
            + "&receiver="
            + userInvitationNotification.getReceiver();
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setTo(recipientAddress);
    simpleMailMessage.setFrom("<mozammaltomal.1001@mail.com>");
    simpleMailMessage.setText("thank you for registration " + url + confirmationUrl);
    simpleMailMessage.setSubject("Invitation confirmatin");
    mailSender.send(simpleMailMessage);
    EmailInvitation emailInvitation =
        new EmailInvitation(
            userInvitationNotification.getSender(),
            userInvitationNotification.getReceiver(),
            userInvitationNotification.getToken());
    emailInvitationRepository.save(emailInvitation);
  }
}
