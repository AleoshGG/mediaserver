package aleosh.online.mediaserver.features.jam.services;

import aleosh.online.mediaserver.features.jam.data.dtos.response.JamResponseDto;

public interface IJamService {
    JamResponseDto createJam(String creatorUsername);
    JamResponseDto joinJam(String joinCode, String username);
    void leaveJam(String joinCode, String username);
}
