package aleosh.online.mediaserver.features.jam.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "jams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // El código corto para el Deep Link (ej. "A4X9BQ")
    @Column(name = "join_code", nullable = false, unique = true, length = 10)
    private String joinCode;

    // Quién tiene el celular donde sale el audio físico
    @Column(name = "active_speaker_user_id", nullable = false)
    private UUID activeSpeakerUserId;

    // El ID de la canción actual en Spotify (se llenará en la fase 3)
    @Column(name = "current_track_id")
    private String currentTrackId;

    // En qué milisegundo va la canción (para sincronizar a los que entran tarde)
    @Column(name = "current_timestamp_ms")
    private Long currentTimestampMs = 0L;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
