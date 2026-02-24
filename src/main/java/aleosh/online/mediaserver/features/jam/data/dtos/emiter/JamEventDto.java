package aleosh.online.mediaserver.features.jam.data.dtos.emiter;

import aleosh.online.mediaserver.features.spotify.data.dtos.response.SpotifyPlayerStateDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JamEventDto {
    private String eventType; // "PLAYING", "PAUSED", "TRACK_CHANGED"
    private String triggeredBy;

    // NUEVO: El estado exacto de la canción en el momento del evento
    private SpotifyPlayerStateDto state;
}