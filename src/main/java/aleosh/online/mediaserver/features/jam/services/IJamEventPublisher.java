package aleosh.online.mediaserver.features.jam.services;

import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyPlayerStateDto;

public interface IJamEventPublisher {
    void broadcastPlaybackState(String joinCode, String eventType, String username, SpotifyPlayerStateDto state);
}