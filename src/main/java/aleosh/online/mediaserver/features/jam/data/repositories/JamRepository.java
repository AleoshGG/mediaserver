package aleosh.online.mediaserver.features.jam.data.repositories;

import aleosh.online.mediaserver.features.jam.data.entities.JamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface JamRepository extends JpaRepository<JamEntity, UUID> {
    Optional<JamEntity> findByJoinCodeAndIsActiveTrue(String joinCode);
}