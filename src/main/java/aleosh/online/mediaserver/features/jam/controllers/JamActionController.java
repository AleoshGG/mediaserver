package aleosh.online.mediaserver.features.jam.controllers;

import aleosh.online.mediaserver.core.dtos.BaseResponse;
import aleosh.online.mediaserver.features.jam.services.IPlaybackOrchestrator;
import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyPlayerStateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jams/{joinCode}/player")
@Tag(name = "Controles de Reproducción", description = "Endpoints para que cualquier miembro de la Jam controle la música.")
public class JamActionController {

    @Autowired
    private final IPlaybackOrchestrator orchestrator;

    public JamActionController(IPlaybackOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Operation(summary = "Reanudar música", description = "Da 'Play' en el dispositivo del altavoz activo.")
    @PutMapping("/play")
    public ResponseEntity<BaseResponse<Void>> playJam(@PathVariable String joinCode) {
        orchestrator.play(joinCode, getAuthenticatedUsername());
        return new BaseResponse<Void>(true, null, "Reproduciendo", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(summary = "Pausar música", description = "Pausa la música en el dispositivo del altavoz activo.")
    @PutMapping("/pause")
    public ResponseEntity<BaseResponse<Void>> pauseJam(@PathVariable String joinCode) {
        orchestrator.pause(joinCode, getAuthenticatedUsername());
        return new BaseResponse<Void>(true, null, "Pausado", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(summary = "Siguiente canción", description = "Salta a la siguiente pista de Spotify.")
    @PostMapping("/next")
    public ResponseEntity<BaseResponse<Void>> nextTrack(@PathVariable String joinCode) {
        orchestrator.next(joinCode, getAuthenticatedUsername());
        return new BaseResponse<Void>(true, null, "Siguiente canción", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(summary = "Canción anterior", description = "Regresa a la pista anterior de Spotify.")
    @PostMapping("/previous")
    public ResponseEntity<BaseResponse<Void>> previousTrack(@PathVariable String joinCode) {
        orchestrator.previous(joinCode, getAuthenticatedUsername());
        return new BaseResponse<Void>(true, null, "Canción anterior", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(summary = "Estado del reproductor", description = "Obtiene la canción actual, la portada y el progreso.")
    @GetMapping("/state")
    public ResponseEntity<BaseResponse<SpotifyPlayerStateDto>> getPlayerState(@PathVariable String joinCode) {
        SpotifyPlayerStateDto state = orchestrator.getCurrentState(joinCode, getAuthenticatedUsername());
        return new BaseResponse<>(true, state, "Estado obtenido", HttpStatus.OK).buildResponseEntity();
    }

    @Operation(summary = "Encolar canción", description = "Añade una canción a la cola de reproducción de la Jam actual.")
    @PostMapping("/queue")
    public ResponseEntity<BaseResponse<Void>> queueTrack(
            @PathVariable String joinCode,
            @RequestParam String trackId) { // Recibimos el ID de la canción por parámetro

        orchestrator.queueTrack(joinCode, getAuthenticatedUsername(), trackId);
        return new BaseResponse<Void>(true, null, "Canción añadida a la cola", HttpStatus.OK).buildResponseEntity();
    }
}