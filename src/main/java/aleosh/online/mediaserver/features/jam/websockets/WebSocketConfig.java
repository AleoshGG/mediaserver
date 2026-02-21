package aleosh.online.mediaserver.features.jam.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // Prioridad alta para que evalúe la seguridad antes de conectar
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketJwtInterceptor jwtInterceptor;

    public WebSocketConfig(WebSocketJwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // La URL raíz a la que se conectará la app móvil. Habilitamos CORS.
        registry.addEndpoint("/ws-jam").setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Los canales públicos/privados empezarán con /topic (ej. /topic/jam/A1B2C3)
        registry.enableSimpleBroker("/topic");
        // Los mensajes que el cliente envíe al servidor (si hiciera falta) empezarán con /app
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Añadimos nuestro cadenero de seguridad
        registration.interceptors(jwtInterceptor);
    }
}