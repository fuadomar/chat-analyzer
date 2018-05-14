package tone.analyzer.service.tone.recognizer;

import io.indico.api.utils.IndicoException;
import tone.analyzer.domain.DTO.*;
import tone.analyzer.domain.model.ChatMessage;

import java.io.IOException;
import java.net.URISyntaxException;

/** Created by mozammal on 4/11/17. */
public interface ToneAnalyzerService {

  public ToneAnalyzerFeedBackDTO analyzeChatToneBetweenSenderAndReceiver(ChatMessage chatMessage);
}
