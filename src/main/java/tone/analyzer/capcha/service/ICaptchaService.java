package tone.analyzer.capcha.service;

/** Created by user on 1/24/2018. */
public interface ICaptchaService {

  void processResponse(final String response) throws Exception;

  String getReCaptchaSite();

  String getReCaptchaSecret();
}
