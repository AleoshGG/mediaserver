package aleosh.online.mediaserver.features.spotify.services;

import java.util.UUID;

public interface ISpotifyAuthService {
    void exchangeCodeForTokens(String code, String username);
    String getValidAccessToken(UUID userId);
    String getValidAccessTokenByUsername(String username);
}
