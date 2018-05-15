package chat.analyzer.service.tone.recognizer;

import chat.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import chat.analyzer.domain.model.ChatMessage;
import chat.analyzer.utility.ChatAnalyzerScorer;

/** Created by mozammal on 4/11/17. */
@Component
public class ToneAnalyzerServiceImpl implements ToneAnalyzerService {

  private static final Logger LOG = LoggerFactory.getLogger(ToneAnalyzerServiceImpl.class);

  @Value("${watson.user.name}")
  private String userName;

  @Value("${watson.user.password}")
  private String password;

  @Autowired private ChatAnalyzerScorer chatAnalyzerScorer;

  @Override
  public ToneAnalyzerFeedBackDTO analyzeChatToneBetweenSenderAndReceiver(ChatMessage chatMessage) {

    return chatAnalyzerScorer.analyzeChatToneBetweenSenderAndReceiverByWatson(chatMessage);
  }
}
