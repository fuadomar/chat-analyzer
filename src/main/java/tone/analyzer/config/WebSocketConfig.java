package tone.analyzer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Created by mozammal on 4/11/17. */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

  /*public class MyHandshakeHandler extends DefaultHandshakeHandler {

      class MyPrincipal implements Principal {

        private User user;

        public MyPrincipal(User user) {
          this.user = user;
        }

        @Override
        public String getName() {
          return String.valueOf(user.getUsername());
        }
      }

      @Override
      protected Principal determineUser(
          ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // add your own code to determine the user
        Principal principal = request.getPrincipal();

        if (principal instanceof OAuth2Authentication) {
          OAuth2Authentication userPrincipal = (OAuth2Authentication) principal;
          org.springframework.security.core.Authentication authentication1 =
              userPrincipal.getUserAuthentication();
          Map<String, String> details = new LinkedHashMap<>();
          details = (Map<String, String>) authentication1.getDetails();
          Object userName = details.get("displayName").replaceAll("\\s+", "");

          return new MyPrincipal((User) userName);
        }

        return principal;
      }
    }
  */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {

    registry.addEndpoint("/stomp").withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic", "/queue/");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.setInterceptors(
        new ChannelInterceptorAdapter() {

          class MyPrincipal extends OAuth2Authentication {

            private User user;

            public MyPrincipal(
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
              String userName;
              if (authentication instanceof OAuth2Authentication) {
                OAuth2Authentication userPrincipal = (OAuth2Authentication) authentication;
                org.springframework.security.core.Authentication authentication1 =
                    userPrincipal.getUserAuthentication();
                Map<String, String> details = new LinkedHashMap<>();
                details = (Map<String, String>) authentication1.getDetails();
                userName = details.get("displayName").replaceAll("\\s+", "");
                Object o = userName;
                accessor.setNativeHeader("chat-user-name", userName);

                User user =
                    new User(
                        userName, "", ((OAuth2Authentication) authentication).getAuthorities());
                MyPrincipal myPrincipal =
                    new MyPrincipal(
                        user,
                        userPrincipal.getOAuth2Request(),
                        userPrincipal.getUserAuthentication());
                accessor.setUser(myPrincipal);
              }
            }
            return message;
          }
        });
  }
}
