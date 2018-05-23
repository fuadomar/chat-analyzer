package chat.analyzer.web;

import chat.analyzer.service.IEmailInvitationService;
import chat.analyzer.auth.service.SecurityService;
import chat.analyzer.auth.service.UserService;
import chat.analyzer.domain.repository.UserAccountRepository;
import java.io.IOException;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import chat.analyzer.capcha.service.IReCaptchaService;
import chat.analyzer.dao.UserAccountDao;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.entity.EmailInvitation;
import chat.analyzer.service.admin.AdminService;
import chat.analyzer.utility.CommonUtility;
import chat.analyzer.validator.UserAccountValidator;
import chat.analyzer.validator.AnonymousInvitationValidator;
import chat.analyzer.validator.EmailInvitationValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.*;

import static java.util.stream.Collectors.joining;

/** Created by mozammal on 4/18/17. */
@Controller
public class UserRegistrationAndChatDetailsWebController {

  private static final Logger LOG =
      LoggerFactory.getLogger(UserRegistrationAndChatDetailsWebController.class);

  private static final String ERROR_ATTRIBUTED = "error";

  private static final String ERROR_MESSAGE_UNSUCCESSFUL_LOGIN =
      "Your username and password is invalid.";

  private static final String MESSAGE_ATTRIBUTED = "message";

  private static final String LOGGED_OUT_SUCCESSFUL_MESSAGE =
      "You have been logged out successfully.";

  private static final String ADMIN_LOGIN_VIEW = "adminLogin";

  private static final String USERS_REGISTRATION_VIEW = "userRegistration";

  private static final String LOGIN_VIEW = "login";

  private static final String CHAT_VIEW = "chat";

  private static final String ADMIN_PANEL_VIEW = "adminPanel";

  private static final String USER_NAME = "userName";

  private static final String USER_LIST = "userList";

  private static final String USER_REGISTRATION_URI = "/userRegistration";

  private static final String LIVE_CHAT_URI = "/chat";

  private static final String ROOT_URI = "/";

  private static final String ACCOUNT_FORM = "userAccountForm";

  private static final String REDIRECT_CHAT_URI = "redirect:/chat";

  private static final String G_RECAPTCHA_RESPONSE = "g-recaptcha-response";

  private static final String ANONYMOUS_USER_REGISTRATION_VIEW = "anonymousUserRegistration";

  private static final String REDIRECT_LOGIN = "redirect:/login";

  private static final String PARAMETER_PASSWORD = "password";

  private static final String PARAMETER_NAME = "name";

  @Autowired
  @Qualifier("securityServiceImpl")
  private SecurityService securityServiceImpl;

  @Autowired
  @Qualifier("anonymousSecurityServiceImpl")
  private SecurityService anonymousSecurityServiceImpl;

  @Autowired
  @Qualifier("userServiceImpl")
  private UserService userServiceImpl;

  @Autowired
  @Qualifier("anonymousUserServiceImpl")
  private UserService anonymousUserServiceImpl;

  @Autowired private UserAccountValidator userAccountValidator;

  @Autowired private EmailInvitationValidator emailInvitationValidator;

  @Autowired private AnonymousInvitationValidator anonymousInvitationValidator;

  @Autowired private AdminService adminService;

  @Autowired private IEmailInvitationService emailInvitationService;

  @Autowired private UserAccountDao userAccountDao;

  @Autowired private CommonUtility commonUtility;

  @Autowired private IReCaptchaService reCaptchaService;

  @Autowired private ServletContext servletContext;

  @RequestMapping(value = "/adminLogin", method = RequestMethod.GET)
  public String adminPanel(Model model) {

    model.addAttribute(ACCOUNT_FORM, new UserAccount());
    return ADMIN_LOGIN_VIEW;
  }

  @RequestMapping(value = USER_REGISTRATION_URI, method = RequestMethod.GET)
  public String registration(Model model) {

    model.addAttribute(ACCOUNT_FORM, new UserAccount());
    model.addAttribute(
        "googleReCapcha", commonUtility.createGoogleReCapchaDivForUerRegistrationPage());
    return USERS_REGISTRATION_VIEW;
  }

  @RequestMapping(value = USER_REGISTRATION_URI, method = RequestMethod.POST)
  public String registration(
      @ModelAttribute("userAccountForm") UserAccount userAccountForm,
      BindingResult bindingResult,
      Model model,
      HttpServletRequest request,
      HttpServletResponse response,
      RedirectAttributes redirectAttributes)
      throws Exception {

    userAccountValidator.validate(userAccountForm, bindingResult);
    if (bindingResult.hasErrors()) {
      return USERS_REGISTRATION_VIEW;
    }

    String googleReCapcha = request.getParameter(G_RECAPTCHA_RESPONSE);
    if (!reCaptchaService.validate(googleReCapcha)) return USERS_REGISTRATION_VIEW;

    String password = userAccountForm.getPassword();
    userServiceImpl.save(userAccountForm);
    securityServiceImpl.autoLogin(userAccountForm.getName(), password, request, response);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserAccount user = userServiceImpl.findByName(auth.getName());
    redirectAttributes.addFlashAttribute(USER_NAME, user.getName());

    return REDIRECT_CHAT_URI;
  }

  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(Model model, HttpServletRequest request, String error, String logout)
      throws MalformedURLException {

    if (error != null) {
      model.addAttribute(ERROR_ATTRIBUTED, ERROR_MESSAGE_UNSUCCESSFUL_LOGIN);
    }
    if (logout != null) {
      model.addAttribute(MESSAGE_ATTRIBUTED, LOGGED_OUT_SUCCESSFUL_MESSAGE);
    }

    String baseUrl = commonUtility.findBaseUrl(request);
    String metaUrl = baseUrl + "/login";
    String meteImageUrl = baseUrl + "/images/default-avatar.png";
    model.addAttribute("metaUrl", metaUrl);
    model.addAttribute("meteImageUrl", meteImageUrl);
    return LOGIN_VIEW;
  }

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(
    value = {ROOT_URI, LIVE_CHAT_URI},
    method = RequestMethod.GET
  )
  public String chat(Model model, HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    UserAccount loggedInUser = null;
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String loggedInUserName = commonUtility.findPrincipalNameFromAuthentication(auth);
    loggedInUser = userAccountDao.findByName(loggedInUserName);
    populateModelForChatView(model, loggedInUser, loggedInUserName);

    String baseUrl = commonUtility.findBaseUrl(request);
    String metaUrl = baseUrl + "/login";
    String meteImageUrl = baseUrl + "images/default-avatar.png";
    model.addAttribute("metaUrl", metaUrl);
    model.addAttribute("meteImageUrl", meteImageUrl);
    return CHAT_VIEW;
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/admin", method = RequestMethod.GET)
  public String admin(Model model) {

    List<UserAccount> userList = adminService.fetchAllUsers();
    model.addAttribute(USER_LIST, userList);
    return ADMIN_PANEL_VIEW;
  }

  @RequestMapping(value = "/confirmationEmail", method = RequestMethod.GET)
  public String confirmationUserByEmail(
      Model model,
      @RequestParam("token") String token,
      @RequestParam("sender") String sender,
      @RequestParam("receiver") String receiver) {

    if (token == null || StringUtils.isBlank((String) token)) {
      return REDIRECT_LOGIN;
    }

    EmailInvitation emailInvitationServiceByToekn =
        emailInvitationService.findByToeknAndSenderAndReceiver(token, sender, receiver);

    if (emailInvitationServiceByToekn == null) {
      return REDIRECT_LOGIN;
    }
    populateModelForUserEmailInviationView(model, emailInvitationServiceByToekn);
    return "userEmailRegistration";
  }

  @RequestMapping(value = "/confirmationEmail", method = RequestMethod.POST)
  public String processEmailConfirmationForm(
      @ModelAttribute("userAccountRegistrationFormByEmail")
          UserAccount userAccountFromRegistrationByEmail,
      BindingResult bindingResult,
      @RequestParam Map requestParams,
      Model model,
      HttpServletRequest request,
      HttpServletResponse response,
      RedirectAttributes redir)
      throws UnsupportedEncodingException, MalformedURLException {

    emailInvitationValidator.validate(userAccountFromRegistrationByEmail, bindingResult);
    if (bindingResult.hasErrors()) {
      LOG.info("error bind error inside method processEmailConfirmationForm: ");
      return REDIRECT_LOGIN;
    }
    EmailInvitation token = emailInvitationService.findByToken((String) requestParams.get("token"));

    if (token == null) {
      return redirectIfInvalidToken(bindingResult, redir);
    }

    Map<String, String> responseParams = new HashMap<>();
    Object userPassword = requestParams.get(PARAMETER_PASSWORD);
    Object userName = requestParams.get(PARAMETER_NAME);

    if (redirectIfUserNameOrPasswordInvalid(userPassword, userName)) {
      return REDIRECT_LOGIN;
    }

    UserAccount userUserAccount = userAccountDao.findByName((String) userName);
    if (userUserAccount != null) {
      boolean matches =
          new BCryptPasswordEncoder().matches((String) userPassword, userUserAccount.getPassword());
      if (!matches) {
        return redirectWithTheSameQueryStringIfPasswordNotMatched(
            requestParams, redir, responseParams);
      }
    }
    tryToLoggedInTheSystem(
        request, response, redir, token, (String) userPassword, (String) userName);
    return "redirect:/chat?invited=" + URLEncoder.encode(token.getSender(), "UTF-8");
  }

  @RequestMapping(value = "/chat/anonymous", method = RequestMethod.GET)
  public String anonymousLoginForChat(Model model, @RequestParam("token") String token) {

    if (token == null || StringUtils.isBlank((String) token)) {
      return REDIRECT_LOGIN;
    }

    EmailInvitation anonymousInvitationToken = emailInvitationService.findByToken(token);

    if (anonymousInvitationToken == null) {
      return REDIRECT_LOGIN;
    }
    model.addAttribute("confirmationToken", anonymousInvitationToken.getToken());
    model.addAttribute("invitedBy", anonymousInvitationToken.getSender());
    return ANONYMOUS_USER_REGISTRATION_VIEW;
  }

  @RequestMapping(value = "/chat/anonymous", method = RequestMethod.POST)
  public String processInvitationForAnonymousUsers(
      @ModelAttribute("accountFromRegistrationByEmail")
          UserAccount userAccountFromRegistrationByEmail,
      BindingResult bindingResult,
      @RequestParam Map requestParams,
      Model model,
      HttpServletRequest request,
      HttpServletResponse response,
      RedirectAttributes redir)
      throws UnsupportedEncodingException {

    String name = null;
    Object userName = requestParams.get("name");
    Map<String, String> responseParams = new HashMap<>();

    anonymousInvitationValidator.validate(userAccountFromRegistrationByEmail, bindingResult);
    if (bindingResult.hasErrors()) {
      LOG.info("error bind error inside method processEmailConfirmationForm: ");
      return REDIRECT_LOGIN;
    }

    if (userName == null || StringUtils.isBlank((String) userName)) {
      return REDIRECT_LOGIN;
    }

    EmailInvitation emailToken =
        emailInvitationService.findByToken((String) requestParams.get("token"));

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserAccount userAccount = userAccountDao.findByName((String) userName);
    if (userAccount == null) {
      userAccount = new UserAccount(((String) userName).trim(), UUID.randomUUID().toString());
    } else if (!commonUtility.isAnonymousUser(userAccount.getRole())) {

      return redirectToTheSameUrlIfUserExistsButNotAnynymousUser(
          requestParams, redir, responseParams);
    }

    userAccountDao.processEmailInvitationAndUpdateBuddyListIfAbsent(
        emailToken, userAccount, anonymousUserServiceImpl);
    anonymousSecurityServiceImpl.autoLogin(
        userAccount.getName(), userAccount.getPassword(), request, response);
    redir.addFlashAttribute(USER_NAME, userName);
    return "redirect:/chat?invited=" + URLEncoder.encode(emailToken.getSender(), "UTF-8");
  }

  private String redirectToTheSameUrlIfUserExistsButNotAnynymousUser(
      @RequestParam Map requestParams,
      RedirectAttributes redir,
      Map<String, String> responseParams) {
    responseParams.put("token", (String) requestParams.get("token"));
    String url = "chat/anonymous?";
    String queryParams =
        responseParams
            .keySet()
            .stream()
            .map(key -> key + "=" + commonUtility.encodeValue(responseParams.get(key)))
            .collect(joining("&", "", ""));
    redir.addFlashAttribute("errorMsg", "Your token has expired or invalid.");
    return "redirect:/" + url + queryParams;
  }

  private void populateModelForChatView(
      Model model, UserAccount loggedInUser, String loggedInUserName) {
    model.addAttribute("userName", loggedInUserName);
    String avatarUri =
        loggedInUser.getDocumentMetaData() != null
            ? loggedInUser.getDocumentMetaData().getThumbNail()
            : null;
    model.addAttribute("userAvatar", avatarUri);
  }

  private void populateModelForUserEmailInviationView(
      Model model, EmailInvitation emailInvitationServiceByToekn) {

    model.addAttribute("confirmationToken", emailInvitationServiceByToekn.getToken());
    model.addAttribute("invitedBy", emailInvitationServiceByToekn.getSender());
    model.addAttribute("invitedTo", emailInvitationServiceByToekn.getReceiver());
    model.addAttribute("userAccountRegistrationFormByEmail", new UserAccount());
  }

  private void tryToLoggedInTheSystem(
      HttpServletRequest request,
      HttpServletResponse response,
      RedirectAttributes redir,
      EmailInvitation token,
      String userPassword,
      String userName) {
    String password = userPassword;
    String name = userName;
    UserAccount account = new UserAccount(name.trim(), password.trim());
    userAccountDao.processEmailInvitationAndUpdateBuddyListIfAbsent(
        token, account, userServiceImpl);

    securityServiceImpl.autoLogin(account.getName(), password, request, response);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserAccount user = userServiceImpl.findByName(auth.getName());
    redir.addFlashAttribute(USER_NAME, user.getName());
  }

  private String redirectWithTheSameQueryStringIfPasswordNotMatched(
      @RequestParam Map requestParams,
      RedirectAttributes redir,
      Map<String, String> responseParams) {
    responseParams.put("token", (String) requestParams.get("token"));
    responseParams.put("sender", (String) requestParams.get("sender"));
    responseParams.put("receiver", (String) requestParams.get("receiver"));
    String url = "confirmationEmail?";
    String queryParams =
        responseParams
            .keySet()
            .stream()
            .map(key -> key + "=" + commonUtility.encodeValue(responseParams.get(key)))
            .collect(joining("&", "", ""));
    redir.addFlashAttribute("errorMsg", "Your token has expired or invalid.");
    return "redirect:/" + url + queryParams;
  }

  private boolean redirectIfUserNameOrPasswordInvalid(Object userPassword, Object userName) {
    if (userPassword == null
        || StringUtils.isBlank((String) userPassword)
        || userName == null
        || StringUtils.isBlank((String) userName)) {
      return true;
    }
    return false;
  }

  private String redirectIfInvalidToken(BindingResult bindingResult, RedirectAttributes redir) {
    bindingResult.reject("password");
    redir.addFlashAttribute("errorMsg", "Your token has expired or invalid.");
    return "redirect:/login";
  }
}
