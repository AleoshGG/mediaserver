package aleosh.online.mediaserver.features.jam.websockets;

import aleosh.online.mediaserver.auth.services.impl.UserDetailsServiceImpl;
import aleosh.online.mediaserver.core.config.jwt.JwtProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class WebSocketJwtInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public WebSocketJwtInterceptor(JwtProvider jwtProvider, UserDetailsServiceImpl userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Solo interceptamos cuando el cliente intenta CONECTARSE por primera vez
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Buscamos el header "Authorization" que manda el móvil
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtProvider.validateToken(token)) {
                    String username = jwtProvider.getUsernameFromToken(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // Si el token es válido, le asignamos la identidad a esta conexión WebSocket
                    accessor.setUser(authentication);
                } else {
                    throw new IllegalArgumentException("Token JWT inválido para el WebSocket");
                }
            }
        }
        return message;
    }
}