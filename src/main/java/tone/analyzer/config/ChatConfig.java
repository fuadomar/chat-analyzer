package tone.analyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import tone.analyzer.domain.repository.ParticipantRepository;
import tone.analyzer.event.PresenceEventListener;

@Configuration
public class ChatConfig {

  public static class Destinations {
    private Destinations() {}

    private static final String LOGIN = "/topic/chat.login";
    private static final String LOGOUT = "/topic/chat.logout";
  }

  private static final int MAX_PROFANITY_LEVEL = 5;

  /*
   * @Bean
   *
   * @Description("Application event multicaster to process events asynchonously"
   * ) public ApplicationEventMulticaster applicationEventMulticaster() {
   * SimpleApplicationEventMulticaster multicaster = new
   * SimpleApplicationEventMulticaster();
   * multicaster.setTaskExecutor(Executors.newFixedThreadPool(10)); return
   * multicaster; }
   */
  @Bean
  @Description("Tracks user presence (join / leave) and broacasts it to all connected users")
  public PresenceEventListener presenceEventListener(SimpMessagingTemplate messagingTemplate) {
    PresenceEventListener presence =
        new PresenceEventListener(messagingTemplate, participantRepository());
    presence.setLoginDestination(Destinations.LOGIN);
    presence.setLogoutDestination(Destinations.LOGOUT);
    return presence;
  }

  @Bean
  @Description("Keeps connected users")
  public ParticipantRepository participantRepository() {
    return new ParticipantRepository();
  }
}
