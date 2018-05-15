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

import tone.analyzer.gateway.UserInvitationGateway;
import tone.analyzer.utility.ChatAnalyzerScorer;
import tone.analyzer.utility.CommonUtility;

/** Created by user on 1/8/2018. */
@RestController
public class UserInvitationRESTController {

  private static final Logger LOG = LoggerFactory.getLogger(UserInvitationRESTController.class);

  @Autowired private CommonUtility commonUtility;

  @Autowired EmailInvitationRepository emailInvitationRepository;

  @Autowired private UserInvitationGateway userInvitationGateway;

  @RequestMapping(value = "/invitationEmail", method = RequestMethod.GET)
  public String inviteUserByEmail(
      @RequestParam("email") String email,
      @RequestParam("invitedText") String invitedText,
      @RequestParam("invitedUser") String invitedUser,
      HttpServletRequest request,
      Principal principal)
      throws MalformedURLException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String sender = commonUtility.findPrincipalNameFromAuthentication(auth);

    if (org.apache.commons.lang3.StringUtils.isBlank(sender)
        || org.apache.commons.lang3.StringUtils.isBlank(email)) {
      return "Error";
    }
    userInvitationGateway.inviteUserByEmail(sender, email, invitedText, invitedUser, request);
    return "Ok";
  }

  @RequestMapping(value = "/anonymousChatLink", method = RequestMethod.GET)
  public String inviteAnonymousUserByGeneratingLink(HttpServletRequest request, Principal principal)
      throws MalformedURLException {

    return userInvitationGateway.inviteAnonymousUserByGeneratingLink(request);
  }
}
