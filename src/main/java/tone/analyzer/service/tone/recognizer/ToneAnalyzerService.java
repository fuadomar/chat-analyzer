package tone.analyzer.service.tone.recognizer;

import io.indico.api.utils.IndicoException;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;

import java.io.IOException;
import java.net.URISyntaxException;

/** Created by mozammal on 4/11/17. */
public interface ToneAnalyzerService {

  public ToneAnalyzerFeedBackDTO analyzerConversationalToneBetweenTwoUser(ChatMessage chatMessage);

  public ToneAnalyzerFeedBackDTO analyzerIndividualConversationalTone(ChatMessage chatMessage);

  public ToneAnalyzerFeedBackDTO analyzeReviewTone(ChatMessage chatMessage);

  public String analyzeIndividualContext(ChatMessage chatMessage)
      throws IOException, IndicoException, URISyntaxException;
}
