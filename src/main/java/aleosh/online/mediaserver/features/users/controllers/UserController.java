package aleosh.online.mediaserver.features.users.controllers;

import aleosh.online.mediaserver.core.dtos.BaseResponse;
import aleosh.online.mediaserver.features.users.data.dtos.request.CreateUserDto;
import aleosh.online.mediaserver.features.users.data.dtos.response.UserResponseDto;
import aleosh.online.mediaserver.features.users.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {this.userService = userService;}

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<UserResponseDto>> createUser(
            @RequestPart("user") CreateUserDto createUserDto,
            @RequestPart("photo")MultipartFile file
            ) {
        UserResponseDto userResponseDto = userService.createUser(createUserDto, file);

        BaseResponse<UserResponseDto> response = new BaseResponse<>(
                true, userResponseDto, "Usuario registrado correctamente", HttpStatus.CREATED
        );

        return response.buildResponseEntity();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponseDto>> getUserById(@PathVariable String id) {
        UserResponseDto user = userService.getUserById(id);

        BaseResponse<UserResponseDto> response = new BaseResponse<>(
                true, user, "Usuario encontrado correctamente", HttpStatus.OK
        );

        return response.buildResponseEntity();
    }

}
