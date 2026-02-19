package aleosh.online.mediaserver.auth.controllers;

import aleosh.online.mediaserver.auth.data.dtos.request.AuthRequestDto;
import aleosh.online.mediaserver.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.mediaserver.auth.services.IAuthService;
import aleosh.online.mediaserver.core.dtos.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;

    @Autowired
    public AuthController(IAuthService authService) {this.authService = authService;}

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponseDto>> login(
            @RequestBody AuthRequestDto authRequestDto
    ) {
        AuthResponseDto authResponseDto = authService.login(authRequestDto);

        BaseResponse<AuthResponseDto> response = new BaseResponse<>(
                true, authResponseDto, "Usuario logueado correctamente", HttpStatus.OK
        );

        return response.buildResponseEntity();
    }

}

