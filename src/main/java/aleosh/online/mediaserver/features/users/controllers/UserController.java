package aleosh.online.apibackend.features.users.controllers;

import aleosh.online.apibackend.core.dtos.response.BaseResponse;
import aleosh.online.apibackend.features.users.data.dtos.request.CreateUserDto;
import aleosh.online.apibackend.features.users.data.dtos.response.UserResponseDto;
import aleosh.online.apibackend.features.users.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {this.userService = userService;}

    @PostMapping
    public ResponseEntity<BaseResponse<UserResponseDto>> createUser(
            @RequestBody CreateUserDto createUserDto
    ) {
        UserResponseDto userResponseDto = userService.createUser(createUserDto);

        BaseResponse<UserResponseDto> response = new BaseResponse<>(
                true, userResponseDto, "Usuario registrado correctamente", HttpStatus.CREATED
        );

        return response.buildResponseEntity();
    }

}
