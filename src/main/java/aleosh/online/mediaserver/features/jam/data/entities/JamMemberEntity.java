package aleosh.online.mediaserver.features.jam.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jam_members", indexes = {
        @Index(name = "idx_jam_user", columnList = "jam_id, user_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JamMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "jam_id", nullable = false)
    private UUID jamId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
}