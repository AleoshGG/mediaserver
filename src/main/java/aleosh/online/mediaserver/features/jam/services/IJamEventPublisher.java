package aleosh.online.mediaserver.features.jam.services;

public interface IJamEventPublisher {
    void broadcastPlaybackState(String joinCode, String eventType, String username);
}
