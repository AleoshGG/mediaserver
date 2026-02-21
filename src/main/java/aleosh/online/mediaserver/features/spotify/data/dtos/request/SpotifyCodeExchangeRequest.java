package aleosh.online.mediaserver.features.spotify.data.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyCodeExchangeRequest {
    @Schema(description = "Código de autorización de Spotify", example = "AQD_...XYZ")
    private String code;
}
