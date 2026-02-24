package aleosh.online.mediaserver.features.spotify.data.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpotifyTrackDto {
    private String id;
    private String name;
    private String artist;
    private String imageUrl;
}
