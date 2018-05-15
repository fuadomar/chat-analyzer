package chat.analyzer.config;

import chat.analyzer.domain.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import chat.analyzer.event.PresenceEventListener;

@Configuration
public class ChatConfig {

  @Value("${app.user.login}")
  private String LOGIN;

  @Value("${app.user.logout}")
  private String LOGOUT;

  @Bean
  @Description("Tracks user presence (join / leave) and broacasts it to all connected users")
  public PresenceEventListener presenceEventListener(SimpMessagingTemplate messagingTemplate) {
    PresenceEventListener presence =
        new PresenceEventListener(messagingTemplate, participantRepository());
    presence.setLoginDestination(LOGIN);
    presence.setLogoutDestination(LOGOUT);
    return presence;
  }

  @Bean
  @Description("Keeps connected users")
  public ParticipantRepository participantRepository() {
    return new ParticipantRepository();
  }
}
