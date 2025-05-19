package co.uptc.config;

import co.uptc.handlers.StompPrincipal;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(@NonNull ServerHttpRequest request, @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        URI uri = request.getURI();
        String query = uri.getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("id=")) {
                    String gameId = param.substring(3);
                    if(!gameId.isBlank()){
                        return new StompPrincipal(gameId);
                    }
                }
            }
        }

        return new StompPrincipal("Anonymous");
    }
}