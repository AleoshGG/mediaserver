package aleosh.online.mediaserver.features.users.services;

import aleosh.online.mediaserver.features.users.data.dtos.request.CreateUserDto;
import aleosh.online.mediaserver.features.users.data.dtos.response.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    UserResponseDto createUser(CreateUserDto createUserDto, MultipartFile file);
    UserResponseDto getUserById(String id);
}
