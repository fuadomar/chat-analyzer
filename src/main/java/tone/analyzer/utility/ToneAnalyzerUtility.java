package tone.analyzer.utility;

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
import tone.analyzer.domain.entity.FlaggedMessage;
import tone.analyzer.domain.entity.Message;
import tone.analyzer.domain.entity.Review;
import tone.analyzer.domain.repository.ConversationRepository;
import tone.analyzer.domain.repository.FlaggedMessageRepository;
import tone.analyzer.domain.repository.MessageRepository;
import tone.analyzer.domain.repository.ReviewRepository;

import java.util.ArrayList;
import java.util.List;

/** Created by mozammal on 4/26/17. */
@Component
public class ToneAnalyzerUtility {

  /*  Anger 0.159056
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

  private static final Logger log = LoggerFactory.getLogger(ToneAnalyzerUtility.class);

  @Value("${watson.user.name}")
  private String userName;

  @Value("${watson.user.password}")
  private String password;

  @Autowired private ConversationRepository conversationRepository;

  @Autowired private MessageRepository messageRepository;

  @Autowired private FlaggedMessageRepository flaggedMessageRepository;

  @Autowired private ReviewRepository reviewRepository;

  public ToneAnalyzerFeedBackDTO analyzeToneBetweenToUserByIBMWatson(ChatMessage chatMessage) {

    ToneAnalyzer toneAnalyzer = getIBMToneAnalyzer();
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

  private ToneAnalyzer getIBMToneAnalyzer() {
    ToneAnalyzer toneAnalyzer = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
    toneAnalyzer.setUsernameAndPassword(userName, password);
    return toneAnalyzer;
  }

  public ToneAnalyzerFeedBackDTO analyzeIndividualToneByIBMWatson(ChatMessage chatMessage) {

    ToneAnalyzer toneAnalyzer = getIBMToneAnalyzer();
    List<Conversation> conversationList =
        conversationRepository.findBySender(chatMessage.getSender());
    if (conversationList == null || conversationList.size() == 0) return null;
    List<Message> messageList = new ArrayList<>();

    for (Conversation conversation : conversationList) {
      messageList.addAll(messageRepository.findAllByConversationId(conversation.getId()));
    }
    String msg = "";
    for (Message message : messageList) msg = msg + message.getContent() + "\n";

    log.info("text: {}", msg);
    List<String> likelyToneInMessage = new ArrayList<>();
    List<Double> likelyToneScore = new ArrayList<>();
    ToneAnalyzerFeedBackDTO toneAnalyzerFeedBackDTO = new ToneAnalyzerFeedBackDTO();
    ToneAnalysis tone = toneAnalyzer.getTone(msg, null).execute();

    for (ToneCategory toneCategory : tone.getDocumentTone().getTones()) {
      for (ToneScore toneScore : toneCategory.getTones()) {
        log.info("{} {} ", toneScore.getName(), toneScore.getScore());
        if (toneScore.getScore() >= 0.5) {
          toneAnalyzerFeedBackDTO.put(toneScore.getName(), toneScore.getScore());
          likelyToneInMessage.add(toneScore.getName());
          likelyToneScore.add(toneScore.getScore());
        }
      }
    }
    if (!likelyToneInMessage.isEmpty()) {
      FlaggedMessage flaggedMessage =
          createFlaggedMessage(chatMessage, msg, likelyToneInMessage, likelyToneScore);
      flaggedMessageRepository.save(flaggedMessage);
    }
    return toneAnalyzerFeedBackDTO;
  }

  private FlaggedMessage createFlaggedMessage(
      ChatMessage chatMessage,
      String msg,
      List<String> likelyToneInMessage,
      List<Double> likelyToneScore) {
    FlaggedMessage flaggedMessage = new FlaggedMessage();
    flaggedMessage.setContent(msg);
    flaggedMessage.setLikelyTone(likelyToneInMessage);
    flaggedMessage.setSender(chatMessage.getSender());
    flaggedMessage.setLikelyToneScore(likelyToneScore);
    return flaggedMessage;
  }

  public ToneAnalyzerFeedBackDTO analyzeReviewToneByIBMWatson(ChatMessage chatMessage) {

    ToneAnalyzer toneAnalyzer = getIBMToneAnalyzer();
    List<Review> reviews = reviewRepository.findByUser(chatMessage.getSender());
    if (reviews == null) return null;
    String reviewedContent = "";
    for (Review review : reviews) reviewedContent = reviewedContent + review.getContent() + "\n";

    log.info("text: {}", reviewedContent);
    ToneAnalyzerFeedBackDTO toneAnalyzerFeedBackDTO = new ToneAnalyzerFeedBackDTO();
    ToneAnalysis tone = toneAnalyzer.getTone(reviewedContent, null).execute();

    for (ToneCategory toneCategory : tone.getDocumentTone().getTones()) {
      for (ToneScore toneScore : toneCategory.getTones()) {
        log.info("{} {} ", toneScore.getName(), toneScore.getScore());
        toneAnalyzerFeedBackDTO.put(toneScore.getName(), toneScore.getScore());
      }
    }
    return toneAnalyzerFeedBackDTO;
  }
}
