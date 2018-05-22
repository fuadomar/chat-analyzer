/*
package tone.analyzer.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import tone.analyzer.auth.service.UserServiceImpl;
import tone.analyzer.domain.entity.UserAccount;

*/
/** *//*

      public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

        public static final String PARAMETER_NAME = "name";

        public static final String SIZE_OF_NAME = "Size.AccountForm.name";

        public static final String DUPLICATE_NAME = "Duplicate.AccountForm.name";

        public static final String EMPTY_NAME = "Null.AccountForm.name";

        public static final String PARAMETER_PASSWORD = "password";

        public static final String NOT_EMPTY_MESSAGE = "NotEmpty.AccountForm";

        public static final String PASSWORD_SIZE_MESSAGE = "Size.AccountForm.password";

        private Pattern pattern;

        private Matcher matcher;

        private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        @Autowired
        private UserServiceImpl userService;

        @Override
        public boolean supports(Class<?> aClass) {
          return UserAccount.class.equals(aClass);
        }

        @Override
        public void validate(Object o, Errors errors) {
          UserAccount account = (UserAccount) o;

          ValidationUtils.rejectIfEmptyOrWhitespace(errors, PARAMETER_NAME, NOT_EMPTY_MESSAGE);
          String name = account.getName();
          if (name.length() > 0) {

            String regex = "([0-9|a-z|A-Z|\\_\\.])+";
            boolean matches = Pattern.matches(regex, account.getName());

            if (userService.findByName(account.getName()) != null) {
              errors.rejectValue(PARAMETER_NAME, "Name not found");
            } else if (!matches) {
              errors.rejectValue(PARAMETER_NAME, EMPTY_NAME);
            }
          }
          if (validateEmail((account.get)))
        }

        private boolean validateEmail(final String email) {
          pattern = Pattern.compile(EMAIL_PATTERN);
          matcher = pattern.matcher(email);
          return matcher.matches();
        }
      }
      */
