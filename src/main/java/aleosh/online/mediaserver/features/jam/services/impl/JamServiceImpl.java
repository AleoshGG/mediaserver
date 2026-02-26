package aleosh.online.mediaserver.features.jam.services.impl;

import aleosh.online.mediaserver.features.jam.data.dtos.response.JamResponseDto;
import aleosh.online.mediaserver.features.jam.data.entities.JamEntity;
import aleosh.online.mediaserver.features.jam.data.entities.JamMemberEntity;
import aleosh.online.mediaserver.features.jam.data.repositories.JamMemberRepository;
import aleosh.online.mediaserver.features.jam.data.repositories.JamRepository;
import aleosh.online.mediaserver.features.jam.services.IJamService;
import aleosh.online.mediaserver.features.users.domain.repositories.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class JamServiceImpl implements IJamService {

    private final JamRepository jamRepository;
    private final JamMemberRepository jamMemberRepository;
    private final IUserRepository userRepository;

    public JamServiceImpl(JamRepository jamRepository, JamMemberRepository jamMemberRepository, IUserRepository userRepository) {
        this.jamRepository = jamRepository;
        this.jamMemberRepository = jamMemberRepository;
        this.userRepository = userRepository;
    }

    private UUID getUserIdByUsername(String username) {
        return userRepository.getByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }

    @Transactional
    @Override
    public JamResponseDto createJam(String creatorUsername) {
        UUID creatorId = getUserIdByUsername(creatorUsername);

        // 1. Crear la sala
        JamEntity jam = new JamEntity();
        jam.setJoinCode(generateShortCode());
        jam.setActiveSpeakerUserId(creatorId); // El que la crea es el altavoz inicial
        jam.setActive(true);
        jam = jamRepository.save(jam);

        // 2. Agregar al creador como miembro
        addMemberToJam(jam.getId(), creatorId);

        return new JamResponseDto(jam.getId(), jam.getJoinCode(), jam.getActiveSpeakerUserId(), jam.isActive());
    }

    @Transactional
    @Override
    public JamResponseDto joinJam(String joinCode, String username) {
        UUID userId = getUserIdByUsername(username);

        // 1. Buscar la sala activa por el código
        JamEntity jam = jamRepository.findByJoinCodeAndIsActiveTrue(joinCode)
                .orElseThrow(() -> new RuntimeException("Jam no encontrada o inactiva"));

        // 2. Si no es miembro ya, lo agregamos
        if (!jamMemberRepository.existsByJamIdAndUserId(jam.getId(), userId)) {
            addMemberToJam(jam.getId(), userId);
        }

        return new JamResponseDto(jam.getId(), jam.getJoinCode(), jam.getActiveSpeakerUserId(), jam.isActive());
    }

    @Override
    @Transactional
    public void leaveJam(String joinCode, String username) {
        UUID userId = getUserIdByUsername(username);

        // 1. Buscar la sala
        JamEntity jam = jamRepository.findByJoinCodeAndIsActiveTrue(joinCode)
                .orElseThrow(() -> new RuntimeException("Jam no encontrada o ya inactiva"));

        // 2. Verificar que el usuario sí esté en la sala
        if (!jamMemberRepository.existsByJamIdAndUserId(jam.getId(), userId)) {
            throw new RuntimeException("El usuario no pertenece a esta Jam");
        }

        // 3. Lo borramos de la sala
        jamMemberRepository.deleteByJamIdAndUserId(jam.getId(), userId);

        // 4. Lógica de "El último apaga la luz" o "Sucesión del trono"
        if (jam.getActiveSpeakerUserId().equals(userId)) {
            // El usuario que se fue era el altavoz. Buscamos si queda alguien más.
            Optional<JamMemberEntity> nextMemberOpt = jamMemberRepository.findFirstByJamIdOrderByJoinedAtAsc(jam.getId());

            if (nextMemberOpt.isPresent()) {
                // Hay herederos: Le pasamos el control al usuario más antiguo
                jam.setActiveSpeakerUserId(nextMemberOpt.get().getUserId());
                jamRepository.save(jam);
            } else {
                // No queda nadie: Cerramos la sala permanentemente
                jam.setActive(false);
                jamRepository.save(jam);
            }
        }
    }

    @Override
    public String generateJoinLink(String joinCode) {
        // 1. Verificamos que la sala exista y siga abierta
        JamEntity jam = jamRepository.findByJoinCodeAndIsActiveTrue(joinCode)
                .orElseThrow(() -> new RuntimeException("No se puede generar enlace: La Jam no existe o ya fue cerrada"));

        // 2. Generamos el Deep Link
        // Nota: En un entorno de producción avanzado, podrías leer "musicjam://" desde el application.properties
        return "musicjam://join/" + jam.getJoinCode();
    }

    private void addMemberToJam(UUID jamId, UUID userId) {
        JamMemberEntity member = new JamMemberEntity();
        member.setJamId(jamId);
        member.setUserId(userId);
        member.setJoinedAt(LocalDateTime.now());
        jamMemberRepository.save(member);
    }

    // Genera un código de 6 caracteres alfanuméricos en mayúsculas
    private String generateShortCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
