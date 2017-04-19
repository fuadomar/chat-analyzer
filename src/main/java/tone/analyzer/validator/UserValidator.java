package tone.analyzer.validator;

/** Created by mozammal on 4/18/17. */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import tone.analyzer.auth.service.UserService;
import tone.analyzer.domain.entity.User;

import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {

  @Autowired private UserService userService;

  @Override
  public boolean supports(Class<?> aClass) {
    return User.class.equals(aClass);
  }

  @Override
  public void validate(Object o, Errors errors) {
    User user = (User) o;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");
    if (user.getEmail().length() < 3 || user.getEmail().length() > 32) {
      errors.rejectValue("email", "Size.UserForm.email");
    }
    String regex = "([0-9|a-z|A-Z|\\_|\\$|\\.|\\s])+";
    boolean matches = Pattern.matches(regex, user.getEmail());

    if (userService.findByEmail(user.getEmail()) != null) {
      errors.rejectValue("email", "Duplicate.UserForm.email");
    } else if (!matches) {
      errors.rejectValue("email", "Null.UserForm.email");
    }

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
    if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
      errors.rejectValue("password", "Size.UserForm.password");
    }
  }
}
