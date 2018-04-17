package tone.analyzer.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.model.UserEmailInvitationNotification;
import tone.analyzer.domain.repository.EmailInvitationRepository;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import tone.analyzer.utility.ToneAnalyzerUtility;

/**
 * Created by user on 1/8/2018.
 */
@RestController
public class UserInvitationRESTController {

    private static final Logger LOG = LoggerFactory.getLogger(UserInvitationRESTController.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private String rabbitmqQueue;

    @Autowired
    private ToneAnalyzerUtility toneAnalyzerUtility;

    @Autowired
    EmailInvitationRepository emailInvitationRepository;

    @RequestMapping(value = "/invitationEmail", method = RequestMethod.GET)
    public String inviteUserByEmail(
            @RequestParam("email") String email, @RequestParam("invitedText") String invitedText,
            @RequestParam("invitedUser") String invitedUser,
            HttpServletRequest request, Principal principal)
            throws MalformedURLException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String sender = toneAnalyzerUtility.findPrincipalNameFromAuthentication(auth);

        if (org.apache.commons.lang3.StringUtils.isBlank(sender)
                || org.apache.commons.lang3.StringUtils.isBlank(email)) {
            return "Error";
        }

        String token = UUID.randomUUID().toString();
        String url = toneAnalyzerUtility.retrieveRootHostUrl(request) + "/confirmationEmail";

        String subject = "Hi " + invitedUser + ", " + "a friend invited you to join chatAnalyzer";
        String confirmationUrl = url + "?token=" + token + "&sender=" + sender + "&receiver=" + email;
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("receiver", email);
        model.put("url", confirmationUrl);
        model.put("invitedText", invitedText);
        model.put("sender", sender);
        UserEmailInvitationNotification newUserInvitationNotification =
                new UserEmailInvitationNotification(subject, token);
        newUserInvitationNotification.setModel(model);
        rabbitTemplate.convertAndSend(rabbitmqQueue, newUserInvitationNotification);
        return "Ok";
    }


    @RequestMapping(value = "/anonymousChatLink", method = RequestMethod.GET)
    public String inviteAnonymousUserByGeneratingLink(
            HttpServletRequest request, Principal principal)
            throws MalformedURLException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String sender = toneAnalyzerUtility.findPrincipalNameFromAuthentication(auth);
        String token = UUID.randomUUID().toString();
        String url = toneAnalyzerUtility.retrieveRootHostUrl(request) + "/chat/anonymous";
        String confirmationUrl = url + "?token=" + token;

        LOG.info(confirmationUrl);
        EmailInvitation emailInvitation =
                new EmailInvitation(
                        sender,
                        "",
                        token);
        emailInvitationRepository.save(emailInvitation);
        return confirmationUrl;
    }
}
