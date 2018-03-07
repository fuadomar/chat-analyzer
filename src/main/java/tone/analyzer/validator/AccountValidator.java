package tone.analyzer.validator;

/**
 * Created by mozammal on 4/18/17.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import tone.analyzer.auth.service.UserService;
import tone.analyzer.domain.entity.Account;

import java.util.regex.Pattern;

@Component
public class AccountValidator implements Validator {

  @Value("${user.name.minimum.length}")
  private Integer USER_NAME_MINIMUM_LENGTH;

  @Value("${user.name.maximum.length}")
  private Integer USER_NAME_MAXIMUM_LENGTH;

  @Value("${user.password.minimum.length}")
  private Integer USER_PASSWORD_MINIMUM_LENGTH;

  @Value("${user.password.maximum.length}")
  private Integer USER_PASSWORD_MAXIMUM_LENGTH;

  public static final String NAME = "name";

  public static final String SIZE_OF_NAME = "Size.AccountForm.name";

  public static final String DUPLICATE_NAME = "Duplicate.AccountForm.name";

  public static final String EMPTY_NAME = "Null.AccountForm.name";

  public static final String PASSWORD = "password";

  public static final String NOT_EMPTY_MESSAGE = "NotEmpty.AccountForm";

  public static final String PASSWORD_SIZE_MESSAGE = "Size.AccountForm.password";

  @Autowired
  private UserService userService;

  @Override
  public boolean supports(Class<?> aClass) {
    return Account.class.equals(aClass);
  }

  @Override
  public void validate(Object o, Errors errors) {
    Account account = (Account) o;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, NAME, NOT_EMPTY_MESSAGE);
    String name = account.getName();
    if (name.length() > 0) {
      if (name.length() < USER_NAME_MINIMUM_LENGTH || name.length() > USER_NAME_MAXIMUM_LENGTH) {
        errors.rejectValue(NAME, SIZE_OF_NAME);
      }
      String regex = "([0-9|a-z|A-Z|\\_\\.])+";
      boolean matches = Pattern.matches(regex, account.getName());

      if (userService.findByName(account.getName()) != null) {
        errors.rejectValue(NAME, DUPLICATE_NAME);
      } else if (!matches) {
        errors.rejectValue(NAME, EMPTY_NAME);
      }
    }

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, PASSWORD, NOT_EMPTY_MESSAGE);

    String password = account.getPassword();
    if (password.length() > 0) {
      if (password.length() < USER_PASSWORD_MINIMUM_LENGTH
          || password.length() > USER_PASSWORD_MAXIMUM_LENGTH) {
        errors.rejectValue(PASSWORD, PASSWORD_SIZE_MESSAGE);
      }
    }
  }
}
