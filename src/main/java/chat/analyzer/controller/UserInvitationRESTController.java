package chat.analyzer.controller;

import chat.analyzer.gateway.UserInvitationGateway;
import chat.analyzer.utility.CommonUtility;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import chat.analyzer.domain.repository.EmailInvitationRepository;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.UUID;

/** Created by user on 1/8/2018. */
@RestController
public class UserInvitationRESTController {

  private static final Logger LOG = LoggerFactory.getLogger(UserInvitationRESTController.class);

  @Autowired private CommonUtility commonUtility;

  @Autowired EmailInvitationRepository emailInvitationRepository;

  @Autowired private UserInvitationGateway userInvitationGateway;

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(value = "/emailInvitation", method = RequestMethod.GET)
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

  @RequestMapping(value = "/anonymousChatUri", method = RequestMethod.GET)
  public String inviteAnonymousUserByGeneratingLink(HttpServletRequest request, Principal principal)
      throws MalformedURLException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String sender = commonUtility.findPrincipalNameFromAuthentication(auth);
    String token = UUID.randomUUID().toString();
    String confirmationUrl = commonUtility.createAnonymousChatUri(token, request);
    userInvitationGateway.inviteAnonymousUserByGeneratingLink(sender, token);
    return confirmationUrl;
  }
}
