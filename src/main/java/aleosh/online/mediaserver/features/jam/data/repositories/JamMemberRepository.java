package aleosh.online.mediaserver.features.jam.data.repositories;

import aleosh.online.mediaserver.features.jam.data.entities.JamMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JamMemberRepository extends JpaRepository<JamMemberEntity, UUID> {
    List<JamMemberEntity> findByJamId(UUID jamId);
    Optional<JamMemberEntity> findByJamIdAndUserId(UUID jamId, UUID userId);
    boolean existsByJamIdAndUserId(UUID jamId, UUID userId);

    // Borrar a un usuario de una sala
    void deleteByJamIdAndUserId(UUID jamId, UUID userId);
    // Obtener al miembro más antiguo para heredarle el altavoz
    Optional<JamMemberEntity> findFirstByJamIdOrderByJoinedAtAsc(UUID jamId);

}