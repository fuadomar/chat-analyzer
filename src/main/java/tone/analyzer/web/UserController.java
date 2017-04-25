package tone.analyzer.web;

import org.springframework.beans.factory.annotation.Autowired;
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
import tone.analyzer.domain.entity.Review;
import tone.analyzer.domain.repository.ReviewRepository;
import tone.analyzer.review.service.ReviewService;
import tone.analyzer.service.AdminService;
import tone.analyzer.validator.UserValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/** Created by mozammal on 4/18/17. */
@Controller
public class UserController {
  @Autowired private UserService userService;

  @Autowired private SecurityService securityService;

  @Autowired private UserValidator userValidator;

  @Autowired private AdminService adminService;

  @Autowired private ReviewService reviewService;

  /*@RequestMapping(value = "/review", method = RequestMethod.GET)
  public String review(Model model) {
    model.addAttribute("reviewForm", new Review());
    return "review";
  }

  @RequestMapping(value = "/review", method = RequestMethod.POST)
  public String storeReview(
      @ModelAttribute("reviewForm") Review reviewForm, BindingResult bindingResult, Model model) {

    reviewService.saveReview(reviewForm);
    return "review";
  }*/

  @RequestMapping(value = "/registration", method = RequestMethod.GET)
  public String registration(Model model) {
    model.addAttribute("userForm", new Account());

    return "registration";
  }

  @RequestMapping(value = "/registration", method = RequestMethod.POST)
  public String registration(
      @ModelAttribute("userForm") Account userForm,
      BindingResult bindingResult,
      Model model,
      HttpServletRequest request,
      HttpServletResponse response,
      RedirectAttributes redirectAttributes) {

    userValidator.validate(userForm, bindingResult);
    ModelAndView modelAndView = new ModelAndView();
    if (bindingResult.hasErrors()) {
      return "registration";
    }
    String plainTextPassword = userForm.getPassword();
    userService.save(userForm);
    securityService.autoLogin(userForm.getName(), plainTextPassword, request, response);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Account user = userService.findByName(auth.getName());
    redirectAttributes.addFlashAttribute("userName", user.getName());
    return "redirect:/chat";
  }

  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(Model model, String error, String logout) {

    if (error != null) model.addAttribute("error", "Your username and password is invalid.");
    if (logout != null) model.addAttribute("message", "You have been logged out successfully.");
    return "login";
  }

  @RequestMapping(
    value = {"/", "/chat"},
    method = RequestMethod.GET
  )
  public String chat(Model model) {

    return "chat";
  }

  @RequestMapping(
    value = {"/admin"},
    method = RequestMethod.GET
  )
  public String admin(Model model) {

    List<Account> userList = adminService.fetchAllUsers();
    model.addAttribute("userList", userList);
    return "admin";
  }
}
