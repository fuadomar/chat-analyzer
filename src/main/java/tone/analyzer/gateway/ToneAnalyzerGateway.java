package tone.analyzer.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import tone.analyzer.service.tone.recognizer.ToneAnalyzerService;

/** Created by mozammal on 4/11/17. */
@Component
public class ToneAnalyzerGateway {

  @Autowired private ToneAnalyzerService toneAnalyzerService;

  public ToneAnalyzerFeedBackDTO analyzerConversationalTone(ChatMessage chatMessage) {

    return toneAnalyzerService.analyzerConversationalToneBetweenTwoUser(chatMessage);
  }

  public ToneAnalyzerFeedBackDTO analyzerIndividualConversationalTone(ChatMessage chatMessage) {

    return toneAnalyzerService.analyzerIndividualConversationalTone(chatMessage);
  }

  public ToneAnalyzerFeedBackDTO analyzeReviewTone(ChatMessage chatMessage) {

    return toneAnalyzerService.analyzeReviewTone(chatMessage);
  }
}
