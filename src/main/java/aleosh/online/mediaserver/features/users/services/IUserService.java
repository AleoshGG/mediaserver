package aleosh.online.mediaserver.features.users.services;

import aleosh.online.mediaserver.features.users.data.dtos.request.CreateUserDto;
import aleosh.online.mediaserver.features.users.data.dtos.response.UserResponseDto;

public interface IUserService {
    UserResponseDto createUser(CreateUserDto createUserDto);
    UserResponseDto getUserById(String id);
}
