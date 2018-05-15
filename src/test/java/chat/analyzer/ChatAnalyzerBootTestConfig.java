package chat.analyzer;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import chat.analyzer.capcha.service.GoogleReCaptchaService;

@Configuration
public class ChatAnalyzerBootTestConfig {

  @Bean
  GoogleReCaptchaService reCaptchaService() {
    return mock(GoogleReCaptchaService.class);
  }
}
