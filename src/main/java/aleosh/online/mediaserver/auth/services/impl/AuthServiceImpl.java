package aleosh.online.mediaserver.auth.services.impl;

import aleosh.online.mediaserver.auth.data.dtos.request.AuthRequestDto;
import aleosh.online.mediaserver.auth.data.dtos.response.AuthResponseDto;
import aleosh.online.mediaserver.auth.services.IAuthService;
import aleosh.online.mediaserver.core.config.jwt.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {

    // Declaramos la variable que faltaba
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    // Inyectamos todo en el constructor
    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtProvider jwtProvider
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        // Ahora sí existe 'authenticationManager'
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        return new AuthResponseDto(jwt);
    }
}
