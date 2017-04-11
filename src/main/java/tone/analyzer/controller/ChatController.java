package tone.analyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tone.analyzer.gateway.ChatGateway;
import tone.analyzer.domain.ChatMessage;

/** Created by mozammal on 4/11/17. */
@RestController
public class ChatController {

  @Autowired private ChatGateway chatGateway;

  @RequestMapping(
    value = "/chat-message/{topic}",
    method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Void> sendChatMesageToDestination(
      @PathVariable("topic") String topic,
      @RequestParam("recipient") String recipient,
      @RequestParam("message") String message,
      UriComponentsBuilder uriComponentsBuilder) {
    ChatMessage chatMessage = new ChatMessage(topic, recipient, message);
    chatGateway.sendMessageTo(chatMessage);
    //messageProducer.sendMessageTo(topic, recipient, message);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(uriComponentsBuilder.buildAndExpand().toUri());
    return new ResponseEntity<Void>(httpHeaders, HttpStatus.CREATED);
  }
}
