package aleosh.online.mediaserver.features.spotify.services;

import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyAlbumDto;
import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyPlayerStateDto;
import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyTrackDto;

import java.util.List;

public interface ISpotifyWebClient {
    void play(String accessToken);
    void pause(String accessToken);
    void nextTrack(String accessToken);

    List<SpotifyAlbumDto> getMySavedAlbums(String accessToken);
    List<SpotifyTrackDto> getMyTopTracks(String accessToken);

    void previousTrack(String accessToken);
    SpotifyPlayerStateDto getCurrentPlaybackState(String accessToken);
}
