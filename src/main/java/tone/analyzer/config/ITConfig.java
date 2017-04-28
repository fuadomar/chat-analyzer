package tone.analyzer.config;

import org.omg.CORBA.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

/** Created by mozammal on 4/27/17. */
@Configuration
public class ITConfig {

  /*   @Autowired
  private Environment env;

  @Bean
  public RemoteWebDriver webDriver() throws MalformedURLException {
      return new RemoteWebDriver(getRemoteUrl(), getDesiredCapabilities());
  }

  private DesiredCapabilities getDesiredCapabilities() {
      return DesiredCapabilities.firefox();
  }

  private URL getRemoteUrl() throws MalformedURLException {
      return new URL("http://localhost:4445/wd/hub");
  }*/
}
