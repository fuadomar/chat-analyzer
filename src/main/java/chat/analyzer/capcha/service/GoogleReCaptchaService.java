package chat.analyzer.capcha.service;

import chat.analyzer.capcha.CaptchaSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.regex.Pattern;

/** Created by user on 1/24/2018. */
@Service
public class GoogleReCaptchaService implements IReCaptchaService {

  @Autowired private HttpServletRequest request;

  @Autowired private CaptchaSettings captchaSettings;

  @Autowired private RestOperations restTemplate;

  private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

  public void processResponse(String response) throws Exception {
    if (!responseSanityCheck(response)) {
      throw new Exception("Response contains invalid characters");
    }

    URI verifyUri =
        URI.create(
            String.format(
                "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
                getReCaptchaSecret(), response, getClientIP()));

    GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);

    if (!googleResponse.isSuccess()) {
      throw new Exception("reCaptcha was not successfully validated");
    }
  }

  private boolean responseSanityCheck(final String response) {
    return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
  }

  public String getReCaptchaSite() {
    return captchaSettings.getSite();
  }

  public String getReCaptchaSecret() {
    return captchaSettings.getSecret();
  }

  private String getClientIP() {
    final String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}
