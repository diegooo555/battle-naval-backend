package co.uptc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final CustomHandshakeInterceptor handshakeInterceptor;
    private final CustomHandshakeHandler handshakeHandler;

    public WebSocketConfig(CustomHandshakeInterceptor handshakeInterceptor, CustomHandshakeHandler handshakeHandler) {
        this.handshakeInterceptor = handshakeInterceptor;
        this.handshakeHandler = handshakeHandler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket")
                .setHandshakeHandler(handshakeHandler)
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("http://localhost:5173");
    }
}