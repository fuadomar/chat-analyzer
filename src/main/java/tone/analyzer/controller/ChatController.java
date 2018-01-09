package tone.analyzer.controller;

import io.indico.api.utils.IndicoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.BuddyDetails;
import tone.analyzer.domain.entity.Message;
import tone.analyzer.domain.repository.AccountRepository;
import tone.analyzer.domain.repository.MessageRepository;
import tone.analyzer.event.LoginEvent;
import tone.analyzer.domain.repository.ParticipantRepository;
import tone.analyzer.gateway.ChatGateway;
import tone.analyzer.domain.model.ChatMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by mozammal on 4/11/17.
 */
@RestController
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatGateway chatGateway;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MessageRepository messageRepository;


    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/fetch/messages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Message> analyzeStatedPlacesTone(@RequestParam("sender") String sender, @RequestParam("receiver") String receiver)
            throws IOException, IndicoException, URISyntaxException {

        Account recipent = accountRepository.findOne(receiver.trim());
        if (recipent == null)
            return null;
        Sort sort = new Sort(Sort.Direction.ASC, "createdTime");
        List<Message> messagesBySenderAndReceiver = messageRepository.findMessagesBySenderAndReceiver(sender, recipent.getName(), sort);

        return messagesBySenderAndReceiver;

    }

    @MessageMapping("/chat-message/message")
    public String sendChatMessageToDestination(ChatMessage chatMessage) {

        chatGateway.sendMessageTo(chatMessage);
        return "Ok";
    }

   /* @SubscribeMapping("/chat.participants")
    public Collection<LoginEvent> retrieveParticipants() {
        log.info("retrieveParticipants method fired");
        return participantRepository.getActiveSessions().values();
    }*/

    @SubscribeMapping("/chat.participants/{userName}")
    public Collection<LoginEvent> retrieveBuddyList(@DestinationVariable String userName) {
        log.info("retrieveParticipants method fired");
        Account userAccount = accountRepository.findByName(userName);
        List<LoginEvent> buddyListObjects = new ArrayList<>();
        Set<BuddyDetails> buddyList = userAccount.getBuddyList();

        if (buddyList == null)
            return buddyListObjects;
        for (BuddyDetails buddy : buddyList) {
            LoginEvent loginEvent = new LoginEvent(buddy.getName(), false);
            loginEvent.setId(buddy.getId());
            buddyListObjects.add(loginEvent);
        }
        List<LoginEvent> activeUser = new ArrayList<>(participantRepository.getActiveSessions().values());

        for (LoginEvent loginEvent : buddyListObjects)
            if (activeUser.contains(loginEvent)) {
                loginEvent.setOnline(true);
            }
        return buddyListObjects;
    }

}
