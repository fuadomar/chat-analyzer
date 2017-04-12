package tone.analyzer.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.ChatMessage;
import tone.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import tone.analyzer.service.ToneAnalyzerService;

/** Created by mozammal on 4/11/17. */
@Component
public class ToneAnalyzerGateway {

  @Autowired private ToneAnalyzerService toneAnalyzerService;

  public ToneAnalyzerFeedBackDTO analyzerConversationalTone(ChatMessage chatMessage) {
    return toneAnalyzerService.analyzerConversationalTone(chatMessage);
  }
}
