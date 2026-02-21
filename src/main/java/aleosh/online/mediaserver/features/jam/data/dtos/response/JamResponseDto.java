package aleosh.online.mediaserver.features.jam.data.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JamResponseDto {
    @Schema(description = "ID interno de la Jam")
    private UUID id;

    @Schema(description = "Código para compartir e invitar", example = "X8B9QA")
    private String joinCode;

    @Schema(description = "ID del usuario que está reproduciendo el audio")
    private UUID activeSpeakerUserId;

    @Schema(description = "Indica si la sala sigue activa")
    private boolean isActive;
}