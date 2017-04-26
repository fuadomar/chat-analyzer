package tone.analyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tone.analyzer.domain.ChatMessage;
import tone.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import tone.analyzer.gateway.ToneAnalyzerGateway;

/** Created by mozammal on 4/11/17. */
@RestController
public class ToneAnalyzerController {

  @Autowired private ToneAnalyzerGateway toneAnalyzerGateway;

  @RequestMapping(value = "/tone-analyzer-between-users", method = RequestMethod.GET)
  public ToneAnalyzerFeedBackDTO analyzerConversationalTone(
      @RequestParam("sender") String sender, @RequestParam("recipient") String recipient) {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    chatMessage.setRecipient(recipient);
    return toneAnalyzerGateway.analyzerConversationalTone(chatMessage);
  }

  @RequestMapping(value = "/tone-analyzer-individual", method = RequestMethod.GET)
  public ToneAnalyzerFeedBackDTO analyzerIndividualConversationalTone(
      @RequestParam("sender") String sender) {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    return toneAnalyzerGateway.analyzerIndividualConversationalTone(chatMessage);
  }

  @RequestMapping(value = "/review-analyzer", method = RequestMethod.GET)
  public ToneAnalyzerFeedBackDTO analyzeReviewTone(@RequestParam("reviewer") String reviewer) {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(reviewer);
    return toneAnalyzerGateway.analyzeReviewTone(chatMessage);
  }
}
