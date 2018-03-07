package tone.analyzer.service.tone.recognizer;

import io.indico.api.utils.IndicoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.DTO.*;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.utility.ToneAnalyzerUtility;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by mozammal on 4/11/17.
 */
@Component
public class ToneAnalyzerServiceImpl implements ToneAnalyzerService {

  private static final Logger log = LoggerFactory.getLogger(ToneAnalyzerServiceImpl.class);

  @Value("${watson.user.name}")
  private String userName;

  @Value("${watson.user.password}")
  private String password;

  @Autowired
  private ToneAnalyzerUtility toneAnalyzerUtility;

  @Override
  public ToneAnalyzerFeedBackDTO analyzerConversationalToneBetweenTwoUser(ChatMessage chatMessage) {

    return toneAnalyzerUtility.analyzeToneBetweenTwoUserByIBMWatson(chatMessage);
  }

  @Override
  public ToneAnalyzerFeedBackDTO analyzerIndividualConversationalTone(ChatMessage chatMessage) {

    return toneAnalyzerUtility.analyzeIndividualToneByIBMWatson(chatMessage);
  }

  @Override
  public ToneAnalyzerFeedBackDTO analyzeReviewTone(ChatMessage chatMessage) {

    return toneAnalyzerUtility.analyzeReviewToneByIBMWatson(chatMessage);
  }

  @Override
  public String analyzeIndividualAspect(ChatMessage chatMessage)
      throws IOException, IndicoException, URISyntaxException {
    return toneAnalyzerUtility.analyzeIndividualAspect(chatMessage);
  }

  @Override
  public OrganizationsDTO analyzeStatedOrganizationsTone(ChatMessage chatMessage)
      throws URISyntaxException, IOException, IndicoException {
    return toneAnalyzerUtility.analyzeStatedOrganizationsTone(chatMessage);
  }

  @Override
  public PlacesDTO analyzeStatedPlacesTone(ChatMessage chatMessage)
      throws URISyntaxException, IOException, IndicoException {
    return toneAnalyzerUtility.analyzeStatedPlaces(chatMessage);
  }

  @Override
  public PeopleDTO analyzeStatedPeopleTone(ChatMessage chatMessage)
      throws IOException, IndicoException {
    return toneAnalyzerUtility.analyzeStatedPeopleTone(chatMessage);
  }

  @Override
  public TextTagDTO analyzeIndividualTextTag(ChatMessage chatMessage)
      throws URISyntaxException, IOException, IndicoException {
    return toneAnalyzerUtility.analyzeIndividualTextTag(chatMessage);
  }
}
