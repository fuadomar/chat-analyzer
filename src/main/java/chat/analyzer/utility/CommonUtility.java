package chat.analyzer.utility;

import chat.analyzer.domain.entity.Role;
import com.google.common.hash.Hashing;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import chat.analyzer.domain.model.UserEmailInvitationNotification;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CommonUtility {

  @Value("${google.recaptcha.key.site}")
  private String recapchaKeySite;

  public String findBaseUrl(HttpServletRequest request) throws MalformedURLException {

    URL requestURL = new URL(request.getRequestURL().toString());
    String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
    return requestURL.getProtocol() + "://" + requestURL.getHost() + port;
  }

  public String findPrincipalNameFromAuthentication(
      org.springframework.security.core.Authentication authentication) {

    if (authentication instanceof OAuth2Authentication) {
      OAuth2Authentication userPrincipal = (OAuth2Authentication) authentication;
      org.springframework.security.core.Authentication authentication1 =
          userPrincipal.getUserAuthentication();
      Map<String, String> details = null;

      if (authentication1.getDetails() instanceof Map<?, ?>) {
        Object emails = ((Map<String, String>) authentication1.getDetails()).get("emails");
        String email = ((List<Map<String, String>>) emails).get(0).get("value");
        return email;
      }
    }
    return authentication.getName();
  }

  public String createImageNameFromUser(String userName, String imageType) {
    String loggedInUserSignature = userName + System.currentTimeMillis();
    String sha256hex =
        Hashing.sha256().hashString(loggedInUserSignature, StandardCharsets.UTF_8).toString();

    if (imageType.equalsIgnoreCase("png")) {
      return sha256hex + UUID.randomUUID().toString() + ".png";
    }

    return "";
  }

  public UserEmailInvitationNotification createEmailTemplate(
      String sender,
      String invitedUser,
      String invitedText,
      String email,
      String url,
      String token) {

    String subject = "Hi " + invitedUser + ", " + "a friend invited you to join chatAnalyzer";
    String confirmationUrl = url + "?token=" + token + "&sender=" + sender + "&receiver=" + email;
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("receiver", email);
    model.put("url", confirmationUrl);
    model.put("invitedText", invitedText);
    model.put("sender", sender);
    UserEmailInvitationNotification newUserInvitationNotification =
        new UserEmailInvitationNotification(subject, token);
    newUserInvitationNotification.setModel(model);

    return newUserInvitationNotification;
  }

  /*<label class="">Captcha</label>
              <div class="g-recaptcha" data-sitekey="6LciqVMUAAAAACY-qsHFcL_E4PRhFowNxOC06pYz"
    data-callback="onReCaptchaSuccess" data-expired-callback="onReCaptchaExpired"></div>
              <span id="captchaError" class="alert alert-danger col-sm-4" style="display:none"></span>
  */
  public String createGoogleReCapchaDivForUerRegistrationPage() {

    String googleReCapchaDiv = "<label class=\"\">Captcha</label>";
    googleReCapchaDiv =
        googleReCapchaDiv
            + "<div class=\"g-recaptcha\" data-sitekey=\""
            + recapchaKeySite
            + "\" data-callback=\"onReCaptchaSuccess\" data-expired-callback=\"onReCaptchaExpired\"></div>";
    googleReCapchaDiv =
        googleReCapchaDiv
            + "<span id=\"captchaError\" class=\"alert alert-danger col-sm-4\" style=\"display:none\"></span><br />";
    return googleReCapchaDiv;
  }

  public String encodeValue(String value) {
    try {
      return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }

  public boolean isAnonymousUser(List<Role> roles) {

    for (Role rol : roles) {
      if (rol.getName().equals("ROLE_ANONYMOUS")) {
        return true;
      }
    }
    return false;
  }

  public String createAnonymousChatUri(String token, HttpServletRequest request)
      throws MalformedURLException {
    String url = findBaseUrl(request) + "/chat/anonymous";
    String confirmationUrl = url + "?token=" + token;
    return confirmationUrl;
  }
}
