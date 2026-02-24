package aleosh.online.mediaserver.features.spotify.controllers;

import aleosh.online.mediaserver.core.dtos.BaseResponse;
import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyAlbumDto;
import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyTrackDto;
import aleosh.online.mediaserver.features.spotify.services.ISpotifyAuthService;
import aleosh.online.mediaserver.features.spotify.services.ISpotifyWebClient;
import aleosh.online.mediaserver.features.users.domain.entities.User;
import aleosh.online.mediaserver.features.users.domain.repositories.IUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/spotify/me")
@Tag(name = "Datos de Spotify", description = "Endpoints para leer el catálogo de música del usuario.")
public class SpotifyDataController {

    private final ISpotifyWebClient spotifyWebClient;
    private final ISpotifyAuthService spotifyAuthService;
    private final IUserRepository userRepository;

    public SpotifyDataController(ISpotifyWebClient spotifyWebClient, ISpotifyAuthService spotifyAuthService, IUserRepository userRepository) {
        this.spotifyWebClient = spotifyWebClient;
        this.spotifyAuthService = spotifyAuthService;
        this.userRepository = userRepository;
    }

    private String getValidUserToken() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.getByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // Utilizamos el motor de tokens eternos que construimos en la Fase 1
        return spotifyAuthService.getValidAccessToken(user.getId());
    }

    @Operation(summary = "Mis Álbumes", description = "Obtiene los últimos álbumes guardados por el usuario.")
    @GetMapping("/albums")
    public ResponseEntity<BaseResponse<List<SpotifyAlbumDto>>> getMyAlbums() {
        List<SpotifyAlbumDto> albums = spotifyWebClient.getMySavedAlbums(getValidUserToken());
        return new BaseResponse<>(true, albums, "Álbumes recuperados", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(summary = "Mi Top de Canciones", description = "Obtiene las canciones más escuchadas recientemente.")
    @GetMapping("/top-tracks")
    public ResponseEntity<BaseResponse<List<SpotifyTrackDto>>> getMyTopTracks() {
        List<SpotifyTrackDto> tracks = spotifyWebClient.getMyTopTracks(getValidUserToken());
        return new BaseResponse<>(true, tracks, "Top tracks recuperado", HttpStatus.OK).buildResponseEntity();
    }
}