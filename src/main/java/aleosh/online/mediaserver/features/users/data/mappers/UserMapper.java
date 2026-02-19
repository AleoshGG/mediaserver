package aleosh.online.mediaserver.features.users.data.mappers;

import aleosh.online.mediaserver.features.users.data.entities.UserEntity;
import aleosh.online.mediaserver.features.users.domain.entities.User;
import org.springframework.stereotype.Component;

@Component("userDataMapper")
public class UserMapper {
    public User toDomain(UserEntity userEntity) {
        if (userEntity == null) { return null; }

        return User.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .photo(userEntity.getPhoto())
                .build();
    }

    public UserEntity toEntity(User user) {
        if (user == null) { return null; }

        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setUsername(user.getUsername());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setPhoto(user.getPhoto());
        return userEntity;
    }
}
