package aleosh.online.mediaserver.features.jam.data.dtos.emiter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JamEventDto {
    private String eventType; // Ej: "PLAYING", "PAUSED", "TRACK_CHANGED"
    private String triggeredBy; // El username del usuario que ejecutó la acción
}