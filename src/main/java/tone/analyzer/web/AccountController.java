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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tone.analyzer.auth.service.SecurityService;
import tone.analyzer.auth.service.UserService;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.service.admin.AdminService;
import tone.analyzer.validator.AccountValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/** Created by mozammal on 4/18/17. */
@Controller
public class AccountController {

  public static final String ERROR_ATTRIBUTED = "error";

  public static final String ERROR_MESSAGE_UNSUCCESSFUL_LOGIN =
      "Your username and password is invalid.";

  public static final String MESSAGE_ATTRIBUTED = "message";

  public static final String LOGGED_OUT_SUCCESSFUL_MESSAGE =
      "You have been logged out successfully.";

  @Autowired private UserService userService;

  @Autowired private SecurityService securityService;

  @Autowired private AccountValidator accountValidator;

  @Autowired private AdminService adminService;

  @RequestMapping(value = "/user-registration", method = RequestMethod.GET)
  public String registration(Model model) {
    model.addAttribute("accountForm", new Account());

    return "registration";
  }

  @RequestMapping(value = "/user-registration", method = RequestMethod.POST)
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
      return "registration";
    }
    String plainTextPassword = accountForm.getPassword();
    userService.save(accountForm);
    securityService.autoLogin(accountForm.getName(), plainTextPassword, request, response);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Account user = userService.findByName(auth.getName());
    redirectAttributes.addFlashAttribute("userName", user.getName());
    return "redirect:/chat";
  }

  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(Model model, String error, String logout) {

    if (error != null) model.addAttribute(ERROR_ATTRIBUTED, ERROR_MESSAGE_UNSUCCESSFUL_LOGIN);
    if (logout != null) model.addAttribute(MESSAGE_ATTRIBUTED, LOGGED_OUT_SUCCESSFUL_MESSAGE);
    return "login";
  }

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(
    value = {"/", "/live-chat"},
    method = RequestMethod.GET
  )
  public String chat(Model model) {

    return "chat";
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/admin", method = RequestMethod.GET)
  public String admin(Model model) {

    List<Account> userList = adminService.fetchAllUsers();
    model.addAttribute("userList", userList);
    return "admin";
  }
}
