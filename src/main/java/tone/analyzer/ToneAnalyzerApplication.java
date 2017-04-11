package tone.analyzer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class ToneAnalyzerApplication {

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

              public void init(FilterConfig filterConfig) {
              }

              public void destroy() {
              }
            });
  }

  public static void main(String[] args) {
    SpringApplication.run(ToneAnalyzerApplication.class, args);
  }
}
