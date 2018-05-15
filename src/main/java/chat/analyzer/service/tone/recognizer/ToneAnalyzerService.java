package chat.analyzer.service.tone.recognizer;

import chat.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import chat.analyzer.domain.model.ChatMessage;

/** Created by mozammal on 4/11/17. */
public interface ToneAnalyzerService {

  public ToneAnalyzerFeedBackDTO analyzeChatToneBetweenSenderAndReceiver(ChatMessage chatMessage);
}
