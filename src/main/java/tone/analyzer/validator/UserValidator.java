package tone.analyzer.validator;

/** Created by mozammal on 4/18/17. */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import tone.analyzer.auth.service.UserService;
import tone.analyzer.domain.entity.Account;

import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {

  @Autowired private UserService userService;

  @Override
  public boolean supports(Class<?> aClass) {
    return Account.class.equals(aClass);
  }

  @Override
  public void validate(Object o, Errors errors) {
    Account user = (Account) o;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty");
    if (user.getName().length() < 3 || user.getName().length() > 32) {
      errors.rejectValue("name", "Size.UserForm.name");
    }
    String regex = "([0-9|a-z|A-Z|\\_\\.])+";
    boolean matches = Pattern.matches(regex, user.getName());

    if (userService.findByName(user.getName()) != null) {
      errors.rejectValue("name", "Duplicate.UserForm.name");
    } else if (!matches) {
      errors.rejectValue("name", "Null.UserForm.name");
    }

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
    if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
      errors.rejectValue("password", "Size.UserForm.password");
    }
  }
}
