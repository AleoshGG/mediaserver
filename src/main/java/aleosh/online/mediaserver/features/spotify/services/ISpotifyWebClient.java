package aleosh.online.mediaserver.features.spotify.services;

public interface ISpotifyWebClient {
    void play(String accessToken);
    void pause(String accessToken);
    void nextTrack(String accessToken);
}
