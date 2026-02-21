package aleosh.online.mediaserver.features.spotify.services.impl;

import aleosh.online.mediaserver.features.spotify.services.ISpotifyWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SpotifyWebClientImpl implements ISpotifyWebClient {

    private final RestTemplate restTemplate;
    private static final String spotify_api_url = "https://api.spotify.com/v1/me/player";

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
        restTemplate.exchange(spotify_api_url + "/play", HttpMethod.PUT, request, Void.class);
    }
    
    @Override
    public void pause(String accessToken) {
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders(accessToken));
        restTemplate.exchange(spotify_api_url + "/pause", HttpMethod.PUT, request, Void.class);
    }
    
    @Override
    public void nextTrack(String accessToken) {
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders(accessToken));
        // Nota: Next es POST, no PUT
        restTemplate.exchange(spotify_api_url + "/next", HttpMethod.POST, request, Void.class);
    }
}