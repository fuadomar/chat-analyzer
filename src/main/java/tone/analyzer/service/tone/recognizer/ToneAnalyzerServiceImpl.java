package tone.analyzer.service.tone.recognizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import tone.analyzer.domain.repository.ConversationRepository;
import tone.analyzer.domain.repository.MessageRepository;
import tone.analyzer.utility.ToneAnalyzerUtility;

/** Created by mozammal on 4/11/17. */
@Component
public class ToneAnalyzerServiceImpl implements ToneAnalyzerService {

  private static final Logger log = LoggerFactory.getLogger(ToneAnalyzerServiceImpl.class);

  @Value("${watson.user.name}")
  private String userName;

  @Value("${watson.user.password}")
  private String password;

  @Autowired private ToneAnalyzerUtility toneAnalyzerUtility;

  @Override
  public ToneAnalyzerFeedBackDTO analyzerConversationalToneBetweenTwoUser(ChatMessage chatMessage) {

    return toneAnalyzerUtility.analyzeToneBetweenToUserByIBMWatson(chatMessage);
  }

  @Override
  public ToneAnalyzerFeedBackDTO analyzerIndividualConversationalTone(ChatMessage chatMessage) {

    return toneAnalyzerUtility.analyzeIndividualToneByIBMWatson(chatMessage);
  }

  @Override
  public ToneAnalyzerFeedBackDTO analyzeReviewTone(ChatMessage chatMessage) {

    return toneAnalyzerUtility.analyzeReviewToneByIBMWatson(chatMessage);
  }
}
