package tone.analyzer.config;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import org.springframework.session.ExpiringSession;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import tone.analyzer.utility.ChatAnalyzerScorer;
import tone.analyzer.utility.CommonUtility;

import java.security.Principal;

/** Created by mozammal on 4/11/17. */
@Configuration
@EnableWebSocketMessageBroker
@EnableScheduling
public class WebSocketConfig
    extends AbstractSessionWebSocketMessageBrokerConfigurer<ExpiringSession> {

  public static final String TOPIC = "/topic/";

  public static final String QUEUE = "/queue/";

  public static final String TOPIC_UNRESOLVED_USER_DEST = "/topic/unresolved.user.dest";

  public static final String TOPIC_REGISTRY_BROADCAST = "/topic/registry.broadcast";

  public static final String APP_PREFIX = "/app";

  public static final String CHAT_USER_NAME = "chat-user-name";

  @Value("${app.relay.host}")
  private String relayHost;

  @Value("${app.relay.port}")
  private Integer relayPort;

  @Value("${raqqbitmq.stomp.user}")
  private String stompUserName;

  @Value("${raqqbitmq.stomp.password}")
  private String stompUserPassword;


  @Override
  protected void configureStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
    stompEndpointRegistry.addEndpoint("/stomp").withSockJS();
  }

  public void configureMessageBroker(MessageBrokerRegistry config) {
    config
        .enableStompBrokerRelay(TOPIC, QUEUE)
        .setUserDestinationBroadcast(TOPIC_UNRESOLVED_USER_DEST)
        .setUserRegistryBroadcast(TOPIC_REGISTRY_BROADCAST)
        .setRelayHost(relayHost)
        .setRelayPort(relayPort)
        .setClientLogin(stompUserName)
        .setClientPasscode(stompUserPassword);
    config.setApplicationDestinationPrefixes(APP_PREFIX);
  }

  public void configureClientInboundChannel(ChannelRegistration registration) {

    registration.setInterceptors(
        new ChannelInterceptorAdapter() {

          class CustomPrincipal extends OAuth2Authentication {

            private User user;

            public CustomPrincipal(
                User user, OAuth2Request oAuth2Request, Authentication authentication) {
              super(oAuth2Request, authentication);
              this.user = user;
            }

            @Override
            public String getName() {
              return String.valueOf(user.getUsername());
            }
          }

          @Override
          public Message<?> preSend(Message<?> message, MessageChannel channel) {

            StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {

              Principal authentication = accessor.getUser();

              if (authentication instanceof OAuth2Authentication) {
                OAuth2Authentication userPrincipal = (OAuth2Authentication) authentication;
                String principalNameFromAuthentication =
                    new CommonUtility()
                        .findPrincipalNameFromAuthentication((Authentication) authentication);
                String displayName = userPrincipal.getName();
                accessor.setNativeHeader(CHAT_USER_NAME, principalNameFromAuthentication);
                User user =
                    new User(
                        principalNameFromAuthentication,
                        new BCryptPasswordEncoder().encode(displayName),
                        ((OAuth2Authentication) authentication).getAuthorities());
                CustomPrincipal customPrincipal =
                    new CustomPrincipal(
                        user,
                        userPrincipal.getOAuth2Request(),
                        userPrincipal.getUserAuthentication());
                accessor.setUser(customPrincipal);
              }
            }
            return message;
          }
        });
  }
}
