package aleosh.online.mediaserver.features.spotify.controllers;

import aleosh.online.mediaserver.core.dtos.BaseResponse;
import aleosh.online.mediaserver.features.spotify.data.dtos.request.SpotifyCodeExchangeRequest;
import aleosh.online.mediaserver.features.spotify.services.ISpotifyAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spotify/auth")
@Tag(name = "Integración Spotify", description = "Endpoints para vincular cuentas de Spotify.")
public class SpotifyAuthController {

    private final ISpotifyAuthService spotifyAuthService;

    @Autowired
    public SpotifyAuthController(ISpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    @Operation(summary = "Vincular cuenta de Spotify", description = "Intercambia el código de Spotify por tokens.")
    @PostMapping("/exchange")
    public ResponseEntity<BaseResponse<String>> exchangeCode(@RequestBody SpotifyCodeExchangeRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        spotifyAuthService.exchangeCodeForTokens(request.getCode(), currentUsername);

        BaseResponse<String> response = new BaseResponse<>(
                true, null, "Cuenta de Spotify vinculada exitosamente", HttpStatus.OK
        );

        return response.buildResponseEntity();
    }
}
