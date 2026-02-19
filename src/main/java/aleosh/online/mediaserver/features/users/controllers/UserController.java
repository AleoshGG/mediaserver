package aleosh.online.mediaserver.features.users.controllers;

import aleosh.online.mediaserver.core.dtos.BaseResponse;
import aleosh.online.mediaserver.features.users.data.dtos.request.CreateUserDto;
import aleosh.online.mediaserver.features.users.data.dtos.response.UserResponseDto;
import aleosh.online.mediaserver.features.users.services.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@Tag(name = "Gestión de usuarios", description = "Endpoints generales para crear y buscar a un usuario.")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {this.userService = userService;}

    @Operation(summary = "Registrar un nuevo usuario",
            description = "Crea un usuario enviando un JSON con los datos y un archivo de imagen opcional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<UserResponseDto>> createUser(
            @Parameter(description = "Datos del usuario (JSON)", required = true, schema = @Schema(implementation = CreateUserDto.class))
            @RequestPart("user") CreateUserDto createUserDto,

            @Parameter(description = "Foto de perfil del usuario", required = true)
            @RequestPart("photo")MultipartFile file
            ) {
        UserResponseDto userResponseDto = userService.createUser(createUserDto, file);

        BaseResponse<UserResponseDto> response = new BaseResponse<>(
                true, userResponseDto, "Usuario registrado correctamente", HttpStatus.CREATED
        );

        return response.buildResponseEntity();
    }

    @Operation(summary = "Obtener usuario por ID", description = "Busca un usuario existente por su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponseDto>> getUserById(
            @Parameter(description = "ID único del usuario", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String id
    ) {
        UserResponseDto user = userService.getUserById(id);

        BaseResponse<UserResponseDto> response = new BaseResponse<>(
                true, user, "Usuario encontrado correctamente", HttpStatus.OK
        );

        return response.buildResponseEntity();
    }

}
