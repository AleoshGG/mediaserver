package aleosh.online.mediaserver.features.jam.services;

public interface IPlaybackOrchestrator {
    void play(String joinCode, String username);
    void pause(String joinCode, String username);
    void next(String joinCode, String username);
}
