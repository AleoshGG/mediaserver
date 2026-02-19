package aleosh.online.mediaserver.features.users.services.mappers;

import aleosh.online.mediaserver.features.users.data.dtos.response.UserResponseDto;
import aleosh.online.mediaserver.features.users.data.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component("userServiceMapper")
public class UserMapper {
    public UserResponseDto toUserResponseDto(UserEntity userEntity) {
        if (userEntity == null) { return null; }

        UserResponseDto dto = new UserResponseDto();
        dto.setId(userEntity.getId());
        dto.setUsername(userEntity.getUsername());
        dto.setEmail(userEntity.getEmail());
        dto.setPhoto(userEntity.getPhoto());
        return dto;
    }

}
