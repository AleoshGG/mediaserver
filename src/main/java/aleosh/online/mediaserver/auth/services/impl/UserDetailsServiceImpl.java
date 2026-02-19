package aleosh.online.mediaserver.auth.services.impl;

import aleosh.online.mediaserver.features.users.domain.entities.User;
import aleosh.online.mediaserver.features.users.domain.repositories.IUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserRepository userRepository;

    public UserDetailsServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscamos el usuario en TU base de datos por email
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con nombre de usuario: " + username ));

        // 2. Gestionamos los roles/permisos (Aquí asumo que tu usuario tiene un rol básico por ahora)
        // Si tu entidad User tiene roles, deberás mapearlos aquí.
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // Ejemplo de rol por defecto

        // 3. Retornamos el objeto User de Spring Security (NO tu entidad, sino el de Spring)
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),// El "username" para Spring
                user.getPassword(),    // La contraseña encriptada de la BD
                true,                  // enabled
                true,                  // accountNonExpired
                true,                  // credentialsNonExpired
                true,                  // accountNonLocked
                authorities            // Lista de roles
        );
    }
}

