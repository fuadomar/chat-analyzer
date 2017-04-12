package tone.analyzer.service;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.ChatMessage;
import tone.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import tone.analyzer.domain.entity.Conversation;
import tone.analyzer.domain.entity.Message;
import tone.analyzer.domain.repository.ConversationRepository;
import tone.analyzer.domain.repository.MessageRepository;

import java.util.List;

/** Created by mozammal on 4/11/17. */
@Component
public class ToneAnalyzerServiceImpl implements ToneAnalyzerService {

  private static final Logger log = LoggerFactory.getLogger(ToneAnalyzerServiceImpl.class);

  @Value("${watson.user.name}")
  private String userName;

  @Value("${watson.user.password}")
  private String password;

  @Autowired private ConversationRepository conversationRepository;

  @Autowired private MessageRepository messageRepository;

  @Override
  public ToneAnalyzerFeedBackDTO analyzerConversationalTone(ChatMessage chatMessage) {
    /*
    ToneAnalysis tone = service.getTone(input, null).execute();

    JsonObject json = parser.parse(tone.toString()).getAsJsonObject();*/

    ToneAnalyzer toneAnalyzer = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
    toneAnalyzer.setUsernameAndPassword(userName, password);
    Conversation conversation =
        conversationRepository.findConversationBySenderAndRecipient(
            chatMessage.getSender(), chatMessage.getRecipient());
    if (conversation == null) return null;
    List<Message> messageList = messageRepository.findAllByConversationId(conversation.getId());

    String msg = "";
    for (Message message : messageList) msg = msg + message.getContent() + "\n";

    log.info("text: {}", msg);
    ToneAnalyzerFeedBackDTO toneAnalyzerFeedBackDTO = new ToneAnalyzerFeedBackDTO();
    ToneAnalysis tone = toneAnalyzer.getTone(msg, null).execute();

    for (ToneCategory toneCategory : tone.getDocumentTone().getTones()) {
      for (ToneScore toneScore : toneCategory.getTones()) {
        log.info("{} {} ", toneScore.getName(), toneScore.getScore());
        toneAnalyzerFeedBackDTO.put(toneScore.getName(), toneScore.getScore());
      }
    }
    return toneAnalyzerFeedBackDTO;
  }
}
