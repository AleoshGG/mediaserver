package aleosh.online.mediaserver.features.jam.services;

import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyPlayerStateDto;

public interface IPlaybackOrchestrator {
    void play(String joinCode, String username);
    void pause(String joinCode, String username);
    void next(String joinCode, String username);

    void previous(String joinCode, String username);
    SpotifyPlayerStateDto getCurrentState(String joinCode, String username);

    void queueTrack(String joinCode, String username, String trackId);
}
