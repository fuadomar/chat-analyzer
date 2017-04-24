package tone.analyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.Role;
import tone.analyzer.domain.repository.AccountRepository;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@EnableOAuth2Client
@SpringBootApplication
@EnableSwagger2
public class ToneAnalyzerApplication {

  @Autowired private AccountRepository userRepository;

  @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Value("${app.admin.name}")
  private String adminName;

  @Value("${app.admin.password}")
  private String plainTextPassword;

  private static final Logger log = LoggerFactory.getLogger(ToneAnalyzerApplication.class);

  @Bean
  FilterRegistrationBean corsFilter(@Value("${tagit.origin}") String origin) {
    return new FilterRegistrationBean(
        new Filter() {
          public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
              throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            String method = request.getMethod();

            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,PUT,DELETE");
            response.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader(
                "Access-Control-Allow-Headers",
                "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
            if ("OPTIONS".equals(method)) {
              response.setStatus(HttpStatus.OK.value());
            } else {
              chain.doFilter(req, res);
            }
          }

          public void init(FilterConfig filterConfig) {}

          public void destroy() {}
        });
  }

  public static void main(String[] args) {
    SpringApplication.run(ToneAnalyzerApplication.class, args);
  }

  @Bean
  CommandLineRunner sendMessage() {
    return args -> {
      Account admin = userRepository.findByName("admin");
      if (admin == null) {
        String encodedPassword = bCryptPasswordEncoder.encode(plainTextPassword);
        admin = new Account(adminName, encodedPassword);
        List<Role> roleList = new ArrayList<>();
        roleList.add(new Role("ROLE_ADMIN"));
        roleList.add(new Role("ROLE_USER"));
        admin.setRole(roleList);
        userRepository.save(admin);
      }
    };
  }
}

@RestController
class PrincipalRestController {
  private static final Logger log = LoggerFactory.getLogger(PrincipalRestController.class);

  @RequestMapping({"/user", "/me"})
  Map<String, String> user(Principal principal) {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("name", principal.getName());
    log.info("redirect from google: " + principal.getName());

    if (principal != null) {
      OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
      org.springframework.security.core.Authentication authentication =
          oAuth2Authentication.getUserAuthentication();
      Map<String, String> details = new LinkedHashMap<>();
      details = (Map<String, String>) authentication.getDetails();
      log.info("details = " + details); // id, email, name, link etc
      String displayName = details.get("displayName").replaceAll("\\s+", "");
    }

    return map;
  }
}
