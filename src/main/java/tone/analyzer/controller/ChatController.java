package tone.analyzer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.repository.AccountRepository;
import tone.analyzer.event.LoginEvent;
import tone.analyzer.domain.repository.ParticipantRepository;
import tone.analyzer.gateway.ChatGateway;
import tone.analyzer.domain.model.ChatMessage;

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
        Set<String> buddyList = userAccount.getBuddyList();

        for (String buddy : buddyList)
            buddyListObjects.add(new LoginEvent(buddy, false));
        List<LoginEvent> activeUser = new ArrayList<>(participantRepository.getActiveSessions().values());

        for (LoginEvent loginEvent : buddyListObjects)
            if (activeUser.contains(loginEvent)) {
                loginEvent.setActive(true);
            }
        return buddyListObjects;
    }

}
