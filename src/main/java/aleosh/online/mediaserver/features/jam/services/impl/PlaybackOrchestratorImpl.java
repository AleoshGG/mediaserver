package aleosh.online.mediaserver.features.jam.services.impl;

import aleosh.online.mediaserver.features.jam.data.entities.JamEntity;
import aleosh.online.mediaserver.features.jam.data.repositories.JamMemberRepository;
import aleosh.online.mediaserver.features.jam.data.repositories.JamRepository;
import aleosh.online.mediaserver.features.jam.services.IJamEventPublisher;
import aleosh.online.mediaserver.features.jam.services.IPlaybackOrchestrator;
import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyPlayerStateDto;
import aleosh.online.mediaserver.features.spotify.services.ISpotifyAuthService;
import aleosh.online.mediaserver.features.spotify.services.ISpotifyWebClient;
import aleosh.online.mediaserver.features.users.domain.repositories.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlaybackOrchestratorImpl implements IPlaybackOrchestrator {

    private final JamRepository jamRepository;
    private final JamMemberRepository jamMemberRepository;
    private final IUserRepository userRepository;

    // Inyectamos los servicios de la Fase 1
    private final ISpotifyAuthService spotifyAuthService;
    private final ISpotifyWebClient spotifyWebClient;

    private IJamEventPublisher  jamEventPublisher;

    public PlaybackOrchestratorImpl(JamRepository jamRepository,
                                    JamMemberRepository jamMemberRepository,
                                    IUserRepository userRepository,
                                    ISpotifyAuthService spotifyAuthService,
                                    ISpotifyWebClient spotifyWebClient,
                                    IJamEventPublisher jamEventPublisher
    ) {
        this.jamRepository = jamRepository;
        this.jamMemberRepository = jamMemberRepository;
        this.userRepository = userRepository;
        this.spotifyAuthService = spotifyAuthService;
        this.spotifyWebClient = spotifyWebClient;
        this.jamEventPublisher = jamEventPublisher;
    }

    // Método central para validar todo antes de tocar Spotify
    private String getSpeakerTokenIfAuthorized(String joinCode, String username) {
        UUID requestUserId = userRepository.getByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();

        JamEntity jam = jamRepository.findByJoinCodeAndIsActiveTrue(joinCode)
                .orElseThrow(() -> new RuntimeException("La Jam no existe o está inactiva"));

        if (!jamMemberRepository.existsByJamIdAndUserId(jam.getId(), requestUserId)) {
            throw new RuntimeException("No puedes controlar la música de una sala a la que no perteneces");
        }

        // Magia: Obtenemos el token del DUEÑO DEL ALTAVOZ, no del que presionó el botón
        return spotifyAuthService.getValidAccessToken(jam.getActiveSpeakerUserId());
    }

    @Override
    public void play(String joinCode, String username) {
        String speakerToken = getSpeakerTokenIfAuthorized(joinCode, username);
        spotifyWebClient.play(speakerToken);

        SpotifyPlayerStateDto state = spotifyWebClient.getCurrentPlaybackState(speakerToken);
        state.setPlaying(true); // Forzamos el valor por si Spotify tarda en reflejarlo
        jamEventPublisher.broadcastPlaybackState(joinCode, "PLAYING", username, state);
    }

    @Override
    public void pause(String joinCode, String username) {
        String speakerToken = getSpeakerTokenIfAuthorized(joinCode, username);
        spotifyWebClient.pause(speakerToken);

        SpotifyPlayerStateDto state = spotifyWebClient.getCurrentPlaybackState(speakerToken);
        state.setPlaying(false); // Forzamos el valor
        jamEventPublisher.broadcastPlaybackState(joinCode, "PAUSED", username, state);
    }

    @Override
    public void next(String joinCode, String username) {
        String speakerToken = getSpeakerTokenIfAuthorized(joinCode, username);
        spotifyWebClient.nextTrack(speakerToken);

        // TRUCO: Esperamos medio segundo para que Spotify actualice su base de datos interna
        try { Thread.sleep(600); } catch (InterruptedException e) {}

        SpotifyPlayerStateDto state = spotifyWebClient.getCurrentPlaybackState(speakerToken);
        jamEventPublisher.broadcastPlaybackState(joinCode, "TRACK_CHANGED", username, state);
    }

    @Override
    public void previous(String joinCode, String username) {
        String speakerToken = getSpeakerTokenIfAuthorized(joinCode, username);
        spotifyWebClient.previousTrack(speakerToken); // Asumiendo que ya agregaste esto en el paso anterior

        // TRUCO: Esperamos medio segundo
        try { Thread.sleep(600); } catch (InterruptedException e) {}

        SpotifyPlayerStateDto state = spotifyWebClient.getCurrentPlaybackState(speakerToken);
        jamEventPublisher.broadcastPlaybackState(joinCode, "TRACK_CHANGED", username, state);
    }

    @Override
    public SpotifyPlayerStateDto getCurrentState(String joinCode, String username) {
        String speakerToken = getSpeakerTokenIfAuthorized(joinCode, username);
        return spotifyWebClient.getCurrentPlaybackState(speakerToken);
    }
}