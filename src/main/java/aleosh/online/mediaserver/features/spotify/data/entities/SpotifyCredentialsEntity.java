package aleosh.online.mediaserver.features.spotify.data.entities;

import aleosh.online.mediaserver.features.spotify.data.converters.CryptoConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "spotify_credentials", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyCredentialsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "access_token", nullable = false, length = 2048)
    private String accessToken;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "refresh_token", nullable = false, length = 2048)
    private String refreshToken;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
