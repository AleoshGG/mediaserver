package aleosh.online.mediaserver.features.spotify.data.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotifyPlayerStateDto {
    private boolean isPlaying;
    private Long progressMs;
    // Reutilizamos el DTO de canción que creamos en el paso anterior
    private SpotifyTrackDto track;
}