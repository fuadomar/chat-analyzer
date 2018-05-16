package chat.analyzer.utility;

import chat.analyzer.dao.UserAccountDao;
import chat.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.model.ChatMessage;
import chat.analyzer.domain.repository.ChatMessageRepository;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;

/** Created by mozammal on 4/26/17. */
@Component
public class ChatAnalyzerScorer {

  /* attributes returned from watson

  Anger 0.159056
  Disgust 0.143912
  Fear 0.405248
  Joy 0.047352
  Sadness 0.301982
  Analytical 0.737339
  Confident 0.0
  Tentative 0.0
  Openness 0.625828
  Conscientiousness 0.418775
  Extraversion 0.687993
  Agreeableness 0.205669
  Emotional Range 0.851521*/

  /**
   * supported aspects domain
   *
   * <p>
   *
   * <p>
   *
   * <p>
   *
   * <p>"hotels" "restaurants" "cars" "airlines" *
   */
  private static final Logger LOG = LoggerFactory.getLogger(ChatAnalyzerScorer.class);

  private double ACCEPTED_WEIGHTED_THRESHOLD = 0.5;

  @Value("${watson.user.name}")
  private String userName;

  @Value("${watson.user.password}")
  private String password;

  @Autowired private ChatMessageRepository chatMessageRepository;

  @Autowired private UserAccountDao userAccountDao;

  public ToneAnalyzerFeedBackDTO analyzeChatToneBetweenSenderAndReceiverByWatson(
      ChatMessage chatChatMessage) {

    ToneAnalyzer toneAnalyzer = getWatsonChatAnalyzer();
    String msg = findAllReceivedMessagesFromSender(chatChatMessage);
    if (msg == null) {
      return null;
    }
    ToneAnalyzerFeedBackDTO toneAnalyzerFeedBackDTO =
        findToneAnalyzerFeedBackDTO(toneAnalyzer, msg);

    return toneAnalyzerFeedBackDTO;
  }

  private String findAllReceivedMessagesFromSender(ChatMessage chatChatMessage) {

    UserAccount sender = userAccountDao.findOne(chatChatMessage.getSender());
    if (sender == null) {
      return null;
    }
    Sort sort = new Sort(Sort.Direction.ASC, "createdTime");
    List<chat.analyzer.domain.entity.ChatMessage> messagesReceivedFromSender =
        chatMessageRepository.findMessagesReceivedFromSender(
            sender.getName(), chatChatMessage.getRecipient(), sort);
    final StringBuilder msg = new StringBuilder();

    messagesReceivedFromSender.forEach(
        receivedMsg -> msg.append(receivedMsg.getContent()).append("\n"));
    LOG.info("msg: {}", msg.toString());
    return msg.toString();
  }

  private ToneAnalyzerFeedBackDTO findToneAnalyzerFeedBackDTO(
      ToneAnalyzer toneAnalyzer, String msg) {
    List<String> probableToneName = new ArrayList<>();
    List<Double> probableToneScore = new ArrayList<>();
    ToneAnalyzerFeedBackDTO toneAnalyzerFeedBackDTO = new ToneAnalyzerFeedBackDTO();
    ToneAnalysis tone = toneAnalyzer.getTone(msg, null).execute();

    /* for (ToneCategory toneCategory : tone.getDocumentTone().getTones()) {*/
    tone.getDocumentTone()
        .getTones()
        .forEach(
            toneCategory -> {
              toneCategory
                  .getTones()
                  .stream()
                  .filter(toneScore -> toneScore.getScore() > ACCEPTED_WEIGHTED_THRESHOLD)
                  .forEach(
                      toneScore -> {
                        toneAnalyzerFeedBackDTO.put(toneScore.getName(), toneScore.getScore());
                        probableToneName.add(toneScore.getName());
                        probableToneScore.add(toneScore.getScore());
                      });
            });
    /*  for (ToneScore toneScore : toneCategory.getTones()) {
      LOG.debug("{} {} ", toneScore.getName(), toneScore.getScore());
      if (toneScore.getScore() >= ACCEPTED_WEIGHTED_THRESHOLD) {
        toneAnalyzerFeedBackDTO.put(toneScore.getName(), toneScore.getScore());
        probableToneName.add(toneScore.getName());
        probableToneScore.add(toneScore.getScore());
      }
    }*/
    return toneAnalyzerFeedBackDTO;
  }

  private ToneAnalyzer getWatsonChatAnalyzer() {
    ToneAnalyzer toneAnalyzer = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
    toneAnalyzer.setUsernameAndPassword(userName, password);
    return toneAnalyzer;
  }
}
