package tone.analyzer.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.model.NewUserInvitationNotification;
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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    private String rabbitmqQueue;

    public String getURLBase(HttpServletRequest request) throws MalformedURLException {

        URL requestURL = new URL(request.getRequestURL().toString());
        String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
        return requestURL.getProtocol() + "://" + requestURL.getHost() + port;

    }

    @RequestMapping(value = "/invitation-email", method = RequestMethod.GET)
    public String inviteUserByEmail(@RequestParam("sender") String sender, @RequestParam("email") String email,
                                    HttpServletRequest request) throws MalformedURLException {

        String token = UUID.randomUUID().toString();
        String url = getURLBase(request) + "/confirmation-email";

        rabbitTemplate.convertAndSend(rabbitmqQueue, new NewUserInvitationNotification(sender, email, token, url));
        return "Ok";
    }

}
