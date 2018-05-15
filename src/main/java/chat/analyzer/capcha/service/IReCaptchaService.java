package chat.analyzer.capcha.service;

/** Created by user on 1/24/2018. */
public interface IReCaptchaService {

  void processResponse(final String response) throws Exception;

  String getReCaptchaSite();

  String getReCaptchaSecret();
}
