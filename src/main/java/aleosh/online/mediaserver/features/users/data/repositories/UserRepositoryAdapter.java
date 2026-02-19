package aleosh.online.mediaserver.features.users.data.repositories;

import aleosh.online.mediaserver.features.users.data.entities.UserEntity;
import aleosh.online.mediaserver.features.users.data.mappers.UserMapper;
import aleosh.online.mediaserver.features.users.domain.entities.User;
import aleosh.online.mediaserver.features.users.domain.repositories.IUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements IUserRepository {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserRepositoryAdapter(
            UserRepository userRepository,
            @org.springframework.beans.factory.annotation.Qualifier("userDataMapper") UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userMapper.toEntity(user);
        UserEntity savedUserEntity = userRepository.save(userEntity);
        return userMapper.toDomain(savedUserEntity);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        return userEntity.map(userMapper::toDomain);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        return userEntity.map(userMapper::toDomain);
    }

    @Override
    public Optional<User> getById(UUID id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        return userEntity.map(userMapper::toDomain);
    }
}
