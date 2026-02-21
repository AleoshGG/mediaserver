package aleosh.online.mediaserver.features.spotify.services.impl;

import aleosh.online.mediaserver.features.spotify.data.entities.SpotifyCredentialsEntity;
import aleosh.online.mediaserver.features.spotify.data.repositories.SpotifyCredentialsRepository;
import aleosh.online.mediaserver.features.spotify.services.ISpotifyAuthService;
import aleosh.online.mediaserver.features.users.domain.entities.User;
import aleosh.online.mediaserver.features.users.domain.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class SpotifyAuthServiceImpl implements ISpotifyAuthService {

    private final SpotifyCredentialsRepository credentialsRepository;
    private final IUserRepository userRepository; // Usamos tu repositorio existente
    private final RestTemplate restTemplate;

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    public SpotifyAuthServiceImpl(SpotifyCredentialsRepository credentialsRepository, IUserRepository userRepository) {
        this.credentialsRepository = credentialsRepository;
        this.userRepository = userRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void exchangeCodeForTokens(String code, String username) {
        // 1. Buscamos el UUID de tu usuario en la BD
        Optional<User> userOpt = userRepository.getByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        UUID userId = userOpt.get().getId();

        String spotifyTokenUrl = "https://accounts.spotify.com/api/token";

        // 2. Preparar Auth de Spotify (Basic Auth)
        String authString = clientId + ":" + clientSecret;
        String base64Auth = Base64.getEncoder().encodeToString(authString.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + base64Auth);

        // 3. Preparar los datos
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // 4. Disparar petición a Spotify
        ResponseEntity<Map> response = restTemplate.postForEntity(spotifyTokenUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            String accessToken = (String) responseBody.get("access_token");
            String refreshToken = (String) responseBody.get("refresh_token");
            Integer expiresIn = (Integer) responseBody.get("expires_in");

            // 5. Guardar en BD
            saveOrUpdateCredentials(userId, accessToken, refreshToken, expiresIn);
        } else {
            throw new RuntimeException("Error al intercambiar código con Spotify");
        }
    }

    @Override
    public String getValidAccessToken(UUID userId) {
        SpotifyCredentialsEntity credentials = credentialsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("El usuario no tiene una cuenta de Spotify vinculada"));

        // Le damos un "colchón" de 5 minutos.
        // Si caduca en menos de 5 minutos (o ya caducó), pedimos uno nuevo.
        if (credentials.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(5))) {
            return refreshAccessToken(credentials);
        }

        // Si todavía está sano y vigente, lo devolvemos directo
        return credentials.getAccessToken();
    }

    private String refreshAccessToken(SpotifyCredentialsEntity credentials) {
        String spotifyTokenUrl = "https://accounts.spotify.com/api/token";

        // 1. Preparar Headers (Igual que en el exchange original)
        String authString = clientId + ":" + clientSecret;
        String base64Auth = Base64.getEncoder().encodeToString(authString.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + base64Auth);

        // 2. Preparar Body (Aquí cambia el grant_type)
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", credentials.getRefreshToken()); // Mandamos el token eterno

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // 3. Disparar petición
        ResponseEntity<Map> response = restTemplate.postForEntity(spotifyTokenUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            String newAccessToken = (String) responseBody.get("access_token");
            Integer expiresIn = (Integer) responseBody.get("expires_in"); // Usualmente 3600 (1 hr)

            // Spotify a veces rota el refresh_token por seguridad, a veces no.
            // Si nos mandan uno nuevo, lo actualizamos.
            if (responseBody.containsKey("refresh_token")) {
                credentials.setRefreshToken((String) responseBody.get("refresh_token"));
            }

            // Actualizamos la entidad
            credentials.setAccessToken(newAccessToken);
            credentials.setExpiresAt(LocalDateTime.now().plusSeconds(expiresIn - 60)); // Colchón de 1 min

            // Guardamos en la BD (pasará por el encriptador automáticamente si hiciste el Paso 1)
            credentialsRepository.save(credentials);

            return newAccessToken;
        } else {
            // Si esto falla, usualmente significa que el usuario revocó el acceso a tu app desde su Spotify
            throw new RuntimeException("No se pudo refrescar el token de Spotify. El usuario debe volver a vincular su cuenta.");
        }
    }

    private void saveOrUpdateCredentials(UUID userId, String accessToken, String refreshToken, Integer expiresIn) {
        Optional<SpotifyCredentialsEntity> existingOpt = credentialsRepository.findByUserId(userId);
        SpotifyCredentialsEntity credentials = existingOpt.orElse(new SpotifyCredentialsEntity());

        credentials.setUserId(userId);
        credentials.setAccessToken(accessToken);
        credentials.setExpiresAt(LocalDateTime.now().plusSeconds(expiresIn - 60));

        if (refreshToken != null) {
            credentials.setRefreshToken(refreshToken);
        }

        credentialsRepository.save(credentials);
    }
}
