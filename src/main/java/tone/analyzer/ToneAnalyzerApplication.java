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
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.Role;
import tone.analyzer.domain.repository.AccountRepository;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@EnableOAuth2Client
@SpringBootApplication
@EnableSwagger2
public class ToneAnalyzerApplication {

  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  public static final String ROLE_USER = "ROLE_USER";

  public static final String SEARCH_BY_ADMIN = "admin";

  @Autowired private AccountRepository accountRepository;

  @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Value("${app.admin.name}")
  private String adminName;

  @Value("${app.admin.password}")
  private String plainTextPassword;

  private static final Logger log = LoggerFactory.getLogger(ToneAnalyzerApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(ToneAnalyzerApplication.class, args);
  }

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

  @Bean
  CommandLineRunner sendMessage() {
    return args -> {
      Account admin = accountRepository.findByName(SEARCH_BY_ADMIN);
      if (admin == null) {
        String encodedPassword = bCryptPasswordEncoder.encode(plainTextPassword);
        admin = new Account(adminName, encodedPassword);
        List<Role> roleList = new ArrayList<>();
        roleList.add(new Role(ROLE_ADMIN));
        roleList.add(new Role(ROLE_USER));
        admin.setRole(roleList);
        accountRepository.save(admin);
      }
    };
  }
}
