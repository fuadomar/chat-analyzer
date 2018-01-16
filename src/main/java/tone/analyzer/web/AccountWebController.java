package tone.analyzer.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tone.analyzer.auth.service.IEmailInvitationService;
import tone.analyzer.auth.service.SecurityService;
import tone.analyzer.auth.service.UserService;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.BuddyDetails;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.repository.AccountRepository;
import tone.analyzer.domain.repository.ParticipantRepository;
import tone.analyzer.event.LoginEvent;
import tone.analyzer.service.admin.AdminService;
import tone.analyzer.validator.AccountValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/** Created by mozammal on 4/18/17. */
@Controller
public class AccountWebController {

  public static final String ERROR_ATTRIBUTED = "error";

  public static final String ERROR_MESSAGE_UNSUCCESSFUL_LOGIN =
      "Your username and password is invalid.";

  public static final String MESSAGE_ATTRIBUTED = "message";

  public static final String LOGGED_OUT_SUCCESSFUL_MESSAGE =
      "You have been logged out successfully.";

  public static final String ADMIN_LOGIN_VIEW = "admin-login";

  public static final String USERS_REGISTRATION_VIEW = "users-registration";

  public static final String LOGIN_VIEW = "login";

  public static final String CHAT_VIEW = "chat";

  public static final String ADMIN_PANEL_VIEW = "admin-panel";

  public static final String USER_NAME = "userName";

  public static final String USER_LIST = "userList";

  public static final String USER_REGISTRATION_URI = "/user-registration";

  public static final String LIVE_CHAT_URI = "/live-chat";

  public static final String ROOT_URI = "/";

  public static final String ACCOUNT_FORM = "accountForm";

  @Autowired private UserService userService;

  @Autowired private SecurityService securityService;

  @Autowired private AccountValidator accountValidator;

  @Autowired private AdminService adminService;

  @Autowired private IEmailInvitationService emailInvitationService;

  @Autowired private AccountRepository accountRepository;

  @Autowired private ParticipantRepository participantRepository;

  public List<LoginEvent> retrieveBuddyList(String userName) {

    Account userAccount = accountRepository.findByName(userName);
    List<LoginEvent> buddyListObjects = new ArrayList<>();
    Set<BuddyDetails> buddyList = userAccount.getBuddyList();

    if (buddyList == null) return buddyListObjects;

    for (BuddyDetails buddy : buddyList) {
      LoginEvent loginEvent = new LoginEvent(buddy.getName(), false);
      loginEvent.setId(buddy.getId());
      buddyListObjects.add(loginEvent);
    }
    List<LoginEvent> activeUser =
        new ArrayList<>(participantRepository.getActiveSessions().values());

    for (LoginEvent loginEvent : buddyListObjects)
      if (activeUser.contains(loginEvent)) {
        loginEvent.setOnline(true);
      }
    return buddyListObjects;
  }

  @RequestMapping(value = "/admin-login", method = RequestMethod.GET)
  public String adminPanel(Model model) {
    model.addAttribute(ACCOUNT_FORM, new Account());

    return ADMIN_LOGIN_VIEW;
  }

  @RequestMapping(value = USER_REGISTRATION_URI, method = RequestMethod.GET)
  public String registration(Model model) {
    model.addAttribute(ACCOUNT_FORM, new Account());

    return USERS_REGISTRATION_VIEW;
  }

  @RequestMapping(value = USER_REGISTRATION_URI, method = RequestMethod.POST)
  public String registration(
      @ModelAttribute("accountForm") Account accountForm,
      BindingResult bindingResult,
      Model model,
      HttpServletRequest request,
      HttpServletResponse response,
      RedirectAttributes redirectAttributes) {

    accountValidator.validate(accountForm, bindingResult);
    ModelAndView modelAndView = new ModelAndView();
    if (bindingResult.hasErrors()) {
      return USERS_REGISTRATION_VIEW;
    }
    String plainTextPassword = accountForm.getPassword();
    userService.save(accountForm);
    securityService.autoLogin(accountForm.getName(), plainTextPassword, request, response);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Account user = userService.findByName(auth.getName());
    redirectAttributes.addFlashAttribute(USER_NAME, user.getName());
    List<LoginEvent> buddyList = retrieveBuddyList(user.getName());
   // redirectAttributes.addAttribute("buddyList", buddyList);
    return "redirect:/live-chat";
  }

  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(Model model, String error, String logout) {

    if (error != null) model.addAttribute(ERROR_ATTRIBUTED, ERROR_MESSAGE_UNSUCCESSFUL_LOGIN);
    if (logout != null) model.addAttribute(MESSAGE_ATTRIBUTED, LOGGED_OUT_SUCCESSFUL_MESSAGE);
    return LOGIN_VIEW;
  }

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(
    value = {ROOT_URI, LIVE_CHAT_URI},
    method = RequestMethod.GET
  )
  public String chat(Model model) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String name = auth.getName();
    model.addAttribute("username", name);
    return CHAT_VIEW;
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/admin", method = RequestMethod.GET)
  public String admin(Model model) {

    List<Account> userList = adminService.fetchAllUsers();
    model.addAttribute(USER_LIST, userList);
    return ADMIN_PANEL_VIEW;
  }
  
  @RequestMapping(value = "/confirmation-email", method = RequestMethod.GET)
  public ModelAndView confirmationUserByEmail(
      ModelAndView modelAndView, @RequestParam("token") String token) {

    EmailInvitation emailInvitationServiceByToekn = emailInvitationService.findByToekn(token);
    modelAndView.addObject("confirmationToken", emailInvitationServiceByToekn.getToken());
    modelAndView.setViewName("confirm");
    return modelAndView;
  }

  // Process confirmation link
  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(value = "/confirmation-email", method = RequestMethod.POST)
  public String processConfirmationForm(
      ModelAndView modelAndView,
      BindingResult bindingResult,
      @RequestParam Map requestParams,
      HttpServletRequest request,
      HttpServletResponse response,
      RedirectAttributes redir)
      throws UnsupportedEncodingException {

    EmailInvitation token = emailInvitationService.findByToekn((String) requestParams.get("token"));

    if (token == null) {
      bindingResult.reject("password");
      redir.addFlashAttribute("errorMessage", "Your password is too weak.  Choose a stronger one.");
      modelAndView.setViewName("redirect:confirm?token=" + requestParams.get("token"));
      return "redirect:confirm?token=" + requestParams.get("token");
    }

    String password = (String) requestParams.get("password");
    Account account = new Account(token.getReceiver(), password);
    Set<BuddyDetails> emailInvitationReceiverBuddyList = account.getBuddyList();

    if (emailInvitationReceiverBuddyList == null) {
      emailInvitationReceiverBuddyList = new HashSet<>();
    }

    Account receiverAccount = userService.findByName(token.getReceiver());
    if (receiverAccount == null) receiverAccount = userService.save(account);

    Account userEmailInvitationSender = userService.findByName(token.getSender());
    Set<BuddyDetails> emailInvitionSenderBuddyList = userEmailInvitationSender.getBuddyList();

    if (emailInvitionSenderBuddyList == null) {
      emailInvitionSenderBuddyList = new HashSet<>();
    }

    emailInvitationReceiverBuddyList.add(
        new BuddyDetails(userEmailInvitationSender.getId(), token.getSender()));
    emailInvitionSenderBuddyList.add(
        new BuddyDetails(receiverAccount.getId(), token.getReceiver()));
    userEmailInvitationSender.setBuddyList(emailInvitionSenderBuddyList);
    userService.addBudyyToUser(userEmailInvitationSender, receiverAccount);

    securityService.autoLogin(account.getName(), password, request, response);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Account user = userService.findByName(auth.getName());
    redir.addFlashAttribute(USER_NAME, user.getName());
    return "redirect:/live-chat?invited=" + URLEncoder.encode(token.getSender(), "UTF-8");
  }
}
