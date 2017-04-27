package tone.analyzer.service.tone.recognizer;

import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;

/** Created by mozammal on 4/11/17. */
public interface ToneAnalyzerService {

  public ToneAnalyzerFeedBackDTO analyzerConversationalToneBetweenTwoUser(ChatMessage chatMessage);

  public ToneAnalyzerFeedBackDTO analyzerIndividualConversationalTone(ChatMessage chatMessage);

  ToneAnalyzerFeedBackDTO analyzeReviewTone(ChatMessage chatMessage);
}
