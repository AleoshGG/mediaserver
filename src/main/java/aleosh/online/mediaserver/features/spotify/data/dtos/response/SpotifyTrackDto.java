package aleosh.online.mediaserver.features.spotify.data.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotifyTrackDto {
    private String id;
    private String name;
    private String artist;
    private String imageUrl;
    private Long durationMs;
}
