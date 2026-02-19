package aleosh.online.mediaserver.features.users.domain.repositories;

import aleosh.online.mediaserver.features.users.domain.entities.User;
import java.util.Optional;
import java.util.UUID;

public interface IUserRepository {
    User save(User user);
    Optional<User> getByEmail(String email);
    Optional<User> getByUsername(String username);
    Optional<User> getById(UUID id);
}
