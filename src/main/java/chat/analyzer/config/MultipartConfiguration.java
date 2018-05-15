package chat.analyzer.config;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Created by Dell on 1/16/2018. */
@Configuration
public class MultipartConfiguration {

  @Bean
  public MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize("54000KB");
    factory.setMaxRequestSize("54000KB");
    return factory.createMultipartConfig();
  }
}
