package tone.analyzer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * Created by mozammal on 4/17/17.
 */
/*
@Configuration
public class WebSocketSecurityConfig extends
        AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpMessageDestMatchers("/topic/chat.login", "/topic/chat.logout", "/topic/chat.message").denyAll()
                .anyMessage().authenticated();
    }
}*/
