package aleosh.online.mediaserver.features.spotify.data.repositories;

import aleosh.online.mediaserver.features.spotify.data.entities.SpotifyCredentialsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpotifyCredentialsRepository extends JpaRepository<SpotifyCredentialsEntity, UUID> {
    Optional<SpotifyCredentialsEntity> findByUserId(UUID userId);
}
