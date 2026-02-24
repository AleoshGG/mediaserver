package aleosh.online.mediaserver.features.spotify.data.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpotifyAlbumDto {
    private String id;
    private String name;
    private String imageUrl;
}