package tone.analyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.repository.EmailInvitationRepository;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by user on 1/8/2018.
 */

@RestController
public class InvitationController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailInvitationRepository emailInvitationRepository;

    public String getURLBase(HttpServletRequest request) throws MalformedURLException {

        URL requestURL = new URL(request.getRequestURL().toString());
        String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
        return requestURL.getProtocol() + "://" + requestURL.getHost() + port;

    }

    @RequestMapping(value = "/invitation-email", method = RequestMethod.GET)
    public String inviteUserByEmail(HttpServletRequest request) throws MalformedURLException {
     /*   User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);*/

        String url = getURLBase(request)+"/confirmation-email";
        String token = UUID.randomUUID().toString();
        String recipientAddress = "mozammaltomal.1001@gmail.com";
        String subject = "Registration Confirmation";
        String sender = "moz1";
        String receiver = "mozammaltomal.1001@gmail.com";
        String confirmationUrl = "?token=" + token + "&sender=" + sender + "&receiver=" + receiver;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("thank you for registration " + url + confirmationUrl);
        mailSender.send(email);
        EmailInvitation emailInvitation = new EmailInvitation(sender, receiver, token);
        emailInvitationRepository.save(emailInvitation);
        return "Ok";
    }

}
