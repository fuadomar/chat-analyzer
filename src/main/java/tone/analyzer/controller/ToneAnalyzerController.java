package tone.analyzer.controller;

import io.indico.api.utils.IndicoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tone.analyzer.domain.DTO.*;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.gateway.ToneAnalyzerGateway;

import java.io.IOException;
import java.net.URISyntaxException;

/** Created by mozammal on 4/11/17. */
@RestController
public class ToneAnalyzerController {

  @Autowired private ToneAnalyzerGateway toneAnalyzerGateway;

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/tone-analyzer-people-individual", method = RequestMethod.GET)
  public PeopleDTO analyzeStatedPeopleTone(@RequestParam("sender") String sender)
      throws IOException, IndicoException, URISyntaxException {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    return toneAnalyzerGateway.analyzeStatedPeopleTone(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/tone-analyzer-places-individual", method = RequestMethod.GET)
  public PlacesDTO analyzeStatedPlacesTone(@RequestParam("sender") String sender)
          throws IOException, IndicoException, URISyntaxException {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    return toneAnalyzerGateway.analyzeStatedPlacesTone(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/tone-analyzer-organizations-individual", method = RequestMethod.GET)
  public OrganizationsDTO analyzeStatedOrganizationsTone(@RequestParam("sender") String sender)
      throws IOException, IndicoException, URISyntaxException {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    return toneAnalyzerGateway.analyzeStatedOrganizationsTone(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/aspect-analyzer-individual", method = RequestMethod.GET)
  public String analyzeIndividualAspect(@RequestParam("sender") String sender)
      throws IOException, IndicoException, URISyntaxException {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    return toneAnalyzerGateway.analyzeIndividualAspect(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/texttag-analyzer-individual", method = RequestMethod.GET)
  public TextTagDTO analyzeIndividualContext(@RequestParam("sender") String sender)
      throws IOException, IndicoException, URISyntaxException {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    return toneAnalyzerGateway.analyzeIndividualTextTag(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/tone-analyzer-between-users", method = RequestMethod.GET)
  public ToneAnalyzerFeedBackDTO analyzerConversationalTone(
      @RequestParam("sender") String sender, @RequestParam("recipient") String recipient) {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    chatMessage.setRecipient(recipient);
    return toneAnalyzerGateway.analyzerConversationalTone(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/tone-analyzer-individual", method = RequestMethod.GET)
  public ToneAnalyzerFeedBackDTO analyzerIndividualConversationalTone(
      @RequestParam("sender") String sender) {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    return toneAnalyzerGateway.analyzerIndividualConversationalTone(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/review-analyzer", method = RequestMethod.GET)
  public ToneAnalyzerFeedBackDTO analyzeReviewTone(@RequestParam("reviewer") String reviewer) {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(reviewer);
    return toneAnalyzerGateway.analyzeReviewTone(chatMessage);
  }
}
