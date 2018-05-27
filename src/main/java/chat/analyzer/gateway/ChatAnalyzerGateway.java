package chat.analyzer.gateway;

import chat.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import chat.analyzer.service.tone.recognizer.ToneAnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import chat.analyzer.domain.DTO.ChatMessageDTO;

/** Created by mozammal on 4/11/17. */
@Component
public class ChatAnalyzerGateway {

  @Autowired private ToneAnalyzerService toneAnalyzerService;

  public ToneAnalyzerFeedBackDTO analyzeChatToneBetweenSenderAndReceiver(
      ChatMessageDTO chatMessageDTO) {

    return toneAnalyzerService.analyzeChatToneBetweenSenderAndReceiver(chatMessageDTO);
  }
}
