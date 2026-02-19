package aleosh.online.mediaserver.features.users.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "Respuesta con la información pública del usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    @Schema(description = "ID del usuario", example = "a1b2c3d434e3fsdf-asdfasdf")
    private UUID id;

    @Schema(description = "Nombre del usuario", example = "alexis-2134")
    private String username;

    @Schema(description = "Correo electrónico del usuario", example = "alexis@gmail.com")
    private String email;

    @Schema(description = "Url de la foto del perfil del usuario", example = "https://aws.bucket.image/ale.png")
    private String photo;
}
