package aleosh.online.mediaserver.auth.services;

import aleosh.online.mediaserver.auth.data.dtos.request.AuthRequestDto;
import aleosh.online.mediaserver.auth.data.dtos.response.AuthResponseDto;

public interface IAuthService {
    AuthResponseDto login(AuthRequestDto authRequestDto);
}
