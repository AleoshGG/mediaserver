package aleosh.online.mediaserver.features.users.services.impl;


import aleosh.online.mediaserver.features.users.data.dtos.request.CreateUserDto;
import aleosh.online.mediaserver.features.users.data.dtos.response.UserResponseDto;
import aleosh.online.mediaserver.features.users.data.entities.UserEntity;
import aleosh.online.mediaserver.features.users.data.repositories.UserRepository;
import aleosh.online.mediaserver.features.users.services.IUserService;
import aleosh.online.mediaserver.features.users.services.mappers.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            @org.springframework.beans.factory.annotation.Qualifier("userServiceMapper")  UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserResponseDto createUser(CreateUserDto createUserDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(createUserDto.getEmail());
        userEntity.setPassword(passwordEncoder.encode(createUserDto.getPassword()));

        UserEntity savedUserEntity = userRepository.save(userEntity);
        return userMapper.toUserResponseDto(savedUserEntity);
    }

    @Override
    public UserResponseDto getUserById(String id) {
        UUID id_UUID = UUID.fromString(id);

        return userRepository.findById(id_UUID)
                .map(userMapper::toUserResponseDto)
                .orElseThrow(()-> new RuntimeException("User not found"));
    }
}
