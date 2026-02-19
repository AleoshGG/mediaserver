package aleosh.online.mediaserver.features.users.data.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo para la creación de un usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    @Schema(description = "Nombre de usuario único", example = "dev_master_99")
    private String username;

    @Schema(description = "Correo electrónico válido", example = "dev@example.com")
    private String email;

    @Schema(description = "Contraseña segura", example = "P4ssw0rd!", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;
}
