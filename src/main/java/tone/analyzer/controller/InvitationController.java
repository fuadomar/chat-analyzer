package tone.analyzer.controller;

import java.security.Principal;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tone.analyzer.domain.model.UserEmailInvitationNotification;
import tone.analyzer.domain.repository.EmailInvitationRepository;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import tone.analyzer.utility.ToneAnalyzerUtility;

/** Created by user on 1/8/2018. */
@RestController
public class InvitationController {

  @Autowired private JavaMailSender mailSender;

  @Autowired private EmailInvitationRepository emailInvitationRepository;

  @Autowired private RabbitTemplate rabbitTemplate;

  @Autowired private Environment environment;

  @Autowired private String rabbitmqQueue;

  @Autowired private ToneAnalyzerUtility toneAnalyzerUtility;

  public String getURLBase(HttpServletRequest request) throws MalformedURLException {

    URL requestURL = new URL(request.getRequestURL().toString());
    String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
    return requestURL.getProtocol() + "://" + requestURL.getHost() + port;
  }

  @RequestMapping(value = "/invitation-email", method = RequestMethod.GET)
  public String inviteUserByEmail(
      @RequestParam("email") String email, HttpServletRequest request, Principal principal)
      throws MalformedURLException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String sender = toneAnalyzerUtility.findPrincipalNameFromAuthentication(auth);

    if (org.apache.commons.lang3.StringUtils.isBlank(sender)
        || org.apache.commons.lang3.StringUtils.isBlank(email)) return "Error";

    String token = UUID.randomUUID().toString();
    String url = getURLBase(request) + "/confirmation-email";

    String subject = "Hi " + email + ", " + "a friend on nascenia invited you to join toneAnalyzer";
    String confirmationUrl = url + "?token=" + token + "&sender=" + sender + "&receiver=" + email;
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("receiver", email);
    model.put("url", confirmationUrl);
    model.put("sender", sender);
    UserEmailInvitationNotification newUserInvitationNotification =
        new UserEmailInvitationNotification(subject, token);
    newUserInvitationNotification.setModel(model);
    rabbitTemplate.convertAndSend(rabbitmqQueue, newUserInvitationNotification);
    return "Ok";
  }
}
