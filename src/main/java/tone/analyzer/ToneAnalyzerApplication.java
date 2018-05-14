package tone.analyzer;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import tone.analyzer.domain.entity.UserAccount;
import tone.analyzer.domain.entity.Role;
import tone.analyzer.domain.repository.UserAccountRepository;

@EnableOAuth2Client
@SpringBootApplication
public class ToneAnalyzerApplication {

  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  public static final String ROLE_USER = "ROLE_USER";

  public static final String SEARCH_BY_ADMIN = "admin";

  public static final String ROLE_ACTUATOR = "ROLE_ACTUATOR";

  @Autowired private UserAccountRepository userAccountRepository;

  @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Value("${app.admin.name}")
  private String adminName;

  @Value("${app.admin.password}")
  private String plainTextPassword;

  private static final Logger LOG = LoggerFactory.getLogger(ToneAnalyzerApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(ToneAnalyzerApplication.class, args);
  }

  @Bean
  CommandLineRunner sendMessage() {
    return args -> {
      UserAccount admin = userAccountRepository.findByName(SEARCH_BY_ADMIN);
      if (admin == null) {
        String encodedPassword = bCryptPasswordEncoder.encode(plainTextPassword);
        admin = new UserAccount(adminName, encodedPassword);
        admin.setRole(
            Arrays.asList(new Role(ROLE_ADMIN), new Role(ROLE_USER), new Role(ROLE_ACTUATOR)));
        userAccountRepository.save(admin);
      }
    };
  }
}
