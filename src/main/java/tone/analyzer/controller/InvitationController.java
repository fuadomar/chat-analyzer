package tone.analyzer.controller;

import org.seleniumhq.jetty9.server.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainer;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tone.analyzer.domain.model.ChatMessage;

import javax.inject.Inject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * Created by user on 1/8/2018.
 */

@RestController
public class InvitationController {

    @Autowired
    private JavaMailSender mailSender;

    @Inject
    private EmbeddedWebApplicationContext appContext;

    @RequestMapping(value = "/invitation-email", method = RequestMethod.GET)
    public String inviteUserByEmail() {
     /*   User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);*/
        String token = UUID.randomUUID().toString();
        String recipientAddress = "mozammaltomal.1001@gmail.com";
        String subject = "Registration Confirmation";
        String confirmationUrl = "?token=" + token;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("thank you for registration " + "http://localhost:8080/confirmation-email" + confirmationUrl);
        mailSender.send(email);

        return "Ok";
    }




}
