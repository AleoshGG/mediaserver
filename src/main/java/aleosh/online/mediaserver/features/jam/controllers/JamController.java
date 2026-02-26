package aleosh.online.mediaserver.features.jam.controllers;

import aleosh.online.mediaserver.core.dtos.BaseResponse;
import aleosh.online.mediaserver.features.jam.data.dtos.response.JamResponseDto;
import aleosh.online.mediaserver.features.jam.services.IJamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jams")
@Tag(name = "Gestión de Jams", description = "Endpoints para crear y unirse a las salas de música compartida.")
public class JamController {

    private final IJamService jamService;

    @Autowired
    public JamController(IJamService jamService) {
        this.jamService = jamService;
    }

    @Operation(summary = "Crear una nueva Jam", description = "Crea una sala y establece al usuario creador como el altavoz inicial.")
    @PostMapping
    public ResponseEntity<BaseResponse<JamResponseDto>> createJam() {
        String currentUsername = getAuthenticatedUsername();
        JamResponseDto jam = jamService.createJam(currentUsername);

        return new BaseResponse<>(true, jam, "Jam creada exitosamente", HttpStatus.CREATED)
                .buildResponseEntity();
    }

    @Operation(summary = "Unirse a una Jam", description = "Usa el código corto (ej. A4X9BQ) para entrar a una sala existente.")
    @PostMapping("/join/{joinCode}")
    public ResponseEntity<BaseResponse<JamResponseDto>> joinJam(@PathVariable String joinCode) {
        String currentUsername = getAuthenticatedUsername();
        JamResponseDto jam = jamService.joinJam(joinCode, currentUsername);

        return new BaseResponse<>(true, jam, "Te has unido a la Jam", HttpStatus.OK)
                .buildResponseEntity();
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Operation(summary = "Abandonar una Jam", description = "Elimina al usuario de la sala. Si la sala queda vacía, se desactiva.")
    @DeleteMapping("/{joinCode}/leave")
    public ResponseEntity<BaseResponse<Void>> leaveJam(@PathVariable String joinCode) {
        String currentUsername = getAuthenticatedUsername();
        jamService.leaveJam(joinCode, currentUsername);

        return new BaseResponse<Void>(true, null, "Has abandonado la Jam correctamente", HttpStatus.OK)
                .buildResponseEntity();
    }

    @Operation(summary = "Obtener enlace de invitación", description = "Genera un Deep Link para invitar a otros usuarios a la Jam.")
    @GetMapping("/{joinCode}/share")
    public ResponseEntity<BaseResponse<String>> getShareLink(@PathVariable String joinCode) {

        String deepLink = jamService.generateJoinLink(joinCode);

        return new BaseResponse<>(true, deepLink, "Enlace de invitación generado", HttpStatus.OK)
                .buildResponseEntity();
    }
}