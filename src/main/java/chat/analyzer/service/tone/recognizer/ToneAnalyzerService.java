package chat.analyzer.service.tone.recognizer;

import chat.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import chat.analyzer.domain.DTO.ChatMessageDTO;

/** Created by mozammal on 4/11/17. */
public interface ToneAnalyzerService {

  public ToneAnalyzerFeedBackDTO analyzeChatToneBetweenSenderAndReceiver(
      ChatMessageDTO chatMessageDTO);
}
