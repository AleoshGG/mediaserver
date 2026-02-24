package aleosh.online.mediaserver.features.jam.services.impl;

import aleosh.online.mediaserver.features.jam.data.dtos.emiter.JamEventDto;
import aleosh.online.mediaserver.features.jam.services.IJamEventPublisher;
import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyPlayerStateDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class JamEventPublisher implements IJamEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public JamEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void broadcastPlaybackState(String joinCode, String eventType, String username, SpotifyPlayerStateDto state) {
        JamEventDto event = new JamEventDto(eventType, username, state);
        messagingTemplate.convertAndSend("/topic/jam/" + joinCode, event);
    }
}