package aleosh.online.mediaserver.features.users.services.impl;

import aleosh.online.mediaserver.features.users.data.dtos.request.CreateUserDto;
import aleosh.online.mediaserver.features.users.data.dtos.response.UserResponseDto;
import aleosh.online.mediaserver.features.users.data.entities.UserEntity;
import aleosh.online.mediaserver.features.users.data.repositories.UserRepository;
import aleosh.online.mediaserver.features.users.services.IStorageService;
import aleosh.online.mediaserver.features.users.services.IUserService;
import aleosh.online.mediaserver.features.users.services.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final IStorageService storageService;

    @Value("${default.photoUrl}")
    private String defaultPhotoUrl;

    public UserServiceImpl(
            UserRepository userRepository,
            @org.springframework.beans.factory.annotation.Qualifier("userServiceMapper")  UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            IStorageService storageService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.storageService = storageService;
    }


    @Override
    public UserResponseDto createUser(CreateUserDto createUserDto, MultipartFile file) {
        String imageKey = null;
        String url = defaultPhotoUrl;

        if (file != null && !file.isEmpty()) {
            try {
                // 1. Subimos la imagen a S3
                imageKey = storageService.uploadFile(file);
                url = storageService.getFileUrl(imageKey);

            } catch (IOException e) {
                throw new RuntimeException("Error al subir la imagen", e);
            }
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(createUserDto.getUsername());
        userEntity.setEmail(createUserDto.getEmail());
        userEntity.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        userEntity.setPhoto(url);

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

    @Override
    public UserResponseDto getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toUserResponseDto)
                .orElseThrow(()-> new RuntimeException("Usuario no encontrado"));
    }
}
