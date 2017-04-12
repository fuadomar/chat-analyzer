package tone.analyzer.service;

import tone.analyzer.domain.ChatMessage;
import tone.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;

/**
 * Created by mozammal on 4/11/17.
 */
public interface ToneAnalyzerService {

    public ToneAnalyzerFeedBackDTO analyzerConversationalTone(ChatMessage chatMessage);
}
