package aleosh.online.mediaserver.features.jam.services.impl;

import aleosh.online.mediaserver.features.jam.data.dtos.emiter.JamEventDto;
import aleosh.online.mediaserver.features.jam.services.IJamEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class JamEventPublisher implements IJamEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public JamEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Método para gritarle a toda la sala
    @Override
    public void broadcastPlaybackState(String joinCode, String eventType, String username) {
        JamEventDto event = new JamEventDto(eventType, username);
        // Enviamos el mensaje al canal específico de esa sala
        messagingTemplate.convertAndSend("/topic/jam/" + joinCode, event);
    }
}