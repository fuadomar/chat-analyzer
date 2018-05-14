package tone.analyzer.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.DTO.*;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.service.tone.recognizer.ToneAnalyzerService;

/** Created by mozammal on 4/11/17. */
@Component
public class ChatAnalyzerGateway {

  @Autowired private ToneAnalyzerService toneAnalyzerService;

  public ToneAnalyzerFeedBackDTO analyzeChatToneBetweenSenderAndReceiver(ChatMessage chatMessage) {

    return toneAnalyzerService.analyzeChatToneBetweenSenderAndReceiver(chatMessage);
  }
}
