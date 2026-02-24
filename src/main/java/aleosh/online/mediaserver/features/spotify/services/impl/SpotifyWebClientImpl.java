package aleosh.online.mediaserver.features.spotify.services.impl;

import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyAlbumDto;
import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyPlayerStateDto;
import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyTrackDto;
import aleosh.online.mediaserver.features.spotify.services.ISpotifyWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class SpotifyWebClientImpl implements ISpotifyWebClient {

    private final RestTemplate restTemplate;
    private static final String spotify_api_url = "https://api.spotify.com/v1";

    public SpotifyWebClientImpl() {
        this.restTemplate = new RestTemplate();
    }

    private HttpHeaders createAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Agrega "Authorization: Bearer {token}"
        return headers;
    }
    
    @Override
    public void play(String accessToken) {
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders(accessToken));
        restTemplate.exchange(spotify_api_url + "/me/player/play", HttpMethod.PUT, request, Void.class);
    }
    
    @Override
    public void pause(String accessToken) {
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders(accessToken));
        restTemplate.exchange(spotify_api_url + "/me/player/pause", HttpMethod.PUT, request, Void.class);
    }
    
    @Override
    public void nextTrack(String accessToken) {
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders(accessToken));
        // Nota: Next es POST, no PUT
        restTemplate.exchange(spotify_api_url + "/me/player/next", HttpMethod.POST, request, Void.class);
    }

    @Override
    public List<SpotifyAlbumDto> getMySavedAlbums(String accessToken) {
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders(accessToken));

        // Pedimos los últimos 10 álbumes guardados
        ResponseEntity<Map> response = restTemplate.exchange(
                spotify_api_url + "/me/albums?limit=10",
                HttpMethod.GET, request, Map.class);

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

        return items.stream().map(item -> {
            Map<String, Object> album = (Map<String, Object>) item.get("album");
            List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");

            return new SpotifyAlbumDto(
                    (String) album.get("id"),
                    (String) album.get("name"),
                    images.isEmpty() ? "" : (String) images.get(0).get("url")
            );
        }).toList();
    }

    @Override
    public List<SpotifyTrackDto> getMyTopTracks(String accessToken) {
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders(accessToken));

        // Pedimos el top 10 de canciones más escuchadas recientemente
        ResponseEntity<Map> response = restTemplate.exchange(
                spotify_api_url + "/me/top/tracks?limit=10&time_range=short_term",
                HttpMethod.GET, request, Map.class);

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

        return items.stream().map(track -> {
            List<Map<String, Object>> artists = (List<Map<String, Object>>) track.get("artists");
            Map<String, Object> album = (Map<String, Object>) track.get("album");
            List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");

            return new SpotifyTrackDto(
                    (String) track.get("id"),
                    (String) track.get("name"),
                    (String) artists.get(0).get("name"),
                    images.isEmpty() ? "" : (String) images.get(0).get("url")
            );
        }).toList();
    }

    @Override
    public void previousTrack(String accessToken) {
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders(accessToken));
        restTemplate.exchange(spotify_api_url + "/me/player/previous", HttpMethod.POST, request, Void.class);
    }

    @Override
    public SpotifyPlayerStateDto getCurrentPlaybackState(String accessToken) {
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders(accessToken));

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    spotify_api_url + "/me/player",
                    HttpMethod.GET, request, Map.class);

            // Si Spotify no está activo o no hay música
            if (response.getBody() == null || response.getStatusCode().value() == 204) {
                return new SpotifyPlayerStateDto(false, 0L, null);
            }

            Map<String, Object> body = response.getBody();
            boolean isPlaying = (Boolean) body.get("is_playing");
            Number progressMs = (Number) body.get("progress_ms");

            Map<String, Object> item = (Map<String, Object>) body.get("item");
            if (item == null) {
                return new SpotifyPlayerStateDto(isPlaying, progressMs.longValue(), null);
            }

            List<Map<String, Object>> artists = (List<Map<String, Object>>) item.get("artists");
            Map<String, Object> album = (Map<String, Object>) item.get("album");
            List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");

            SpotifyTrackDto track = new SpotifyTrackDto(
                    (String) item.get("id"),
                    (String) item.get("name"),
                    (String) artists.get(0).get("name"),
                    images.isEmpty() ? "" : (String) images.get(0).get("url")
            );

            return new SpotifyPlayerStateDto(isPlaying, progressMs.longValue(), track);

        } catch (Exception e) {
            // Si hay cualquier otro error, asumimos que no hay nada sonando
            return new SpotifyPlayerStateDto(false, 0L, null);
        }
    }

    @Override
    public List<SpotifyTrackDto> searchTracks(String accessToken, String query) {
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders(accessToken));

        // Usamos {query} como variable para que RestTemplate codifique los espacios automáticamente
        String urlTemplate = spotify_api_url + "/search?q={query}&type=track&limit=2";

        // Pasamos 'query' como último parámetro
        ResponseEntity<Map> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                request,
                Map.class,
                query
        );

        Map<String, Object> tracksNode = (Map<String, Object>) response.getBody().get("tracks");
        List<Map<String, Object>> items = (List<Map<String, Object>>) tracksNode.get("items");

        return items.stream().map(track -> {
            List<Map<String, Object>> artists = (List<Map<String, Object>>) track.get("artists");
            Map<String, Object> album = (Map<String, Object>) track.get("album");
            List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");

            return new SpotifyTrackDto(
                    (String) track.get("id"),
                    (String) track.get("name"),
                    (String) artists.get(0).get("name"),
                    images.isEmpty() ? "" : (String) images.get(0).get("url")
            );
        }).toList();
    }

    @Override
    public void queueTrack(String accessToken, String trackUri) {
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders(accessToken));

        // Agrega una pista a la cola del usuario usando su URI (ej. spotify:track:4iV5W9uYEdYUVa79Axb7Rh)
        restTemplate.exchange(
                spotify_api_url + "/me/player/queue?uri=" + trackUri,
                HttpMethod.POST, request, Void.class);
    }
}