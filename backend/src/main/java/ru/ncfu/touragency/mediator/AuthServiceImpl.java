package ru.ncfu.touragency.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.touragency.dto.request.LoginRequest;
import ru.ncfu.touragency.dto.request.RegisterRequest;
import ru.ncfu.touragency.dto.response.AuthResponse;
import ru.ncfu.touragency.entity.Role;
import ru.ncfu.touragency.entity.User;
import ru.ncfu.touragency.entity.enums.RoleName;
import ru.ncfu.touragency.exception.BusinessRuleException;
import ru.ncfu.touragency.foundation.RoleRepository;
import ru.ncfu.touragency.foundation.UserRepository;
import ru.ncfu.touragency.security.JwtUtils;
import ru.ncfu.touragency.security.UserDetailsImpl;

import java.util.List;

/**
 * Mediator-слой (PCMEF) — реализация бизнес-логики аутентификации
 * и регистрации. Координирует Entity (User, Role) и Foundation (репозитории),
 * ничего не знает о HTTP/Presentation.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Пользователь с таким email уже зарегистрирован");
        }

        User user = new User(request.fullName(), request.email(),
                passwordEncoder.encode(request.password()), request.phone());

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_USER)));
        user.addRole(userRole);

        User saved = userRepository.save(user);
        String token = jwtUtils.generateToken(saved.getEmail(), saved.getId());

        return new AuthResponse(token, saved.getId(), saved.getFullName(), saved.getEmail(),
                List.of(RoleName.ROLE_USER.name()));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtils.generateToken(principal.getUsername(), principal.getId());

        List<String> roles = principal.getAuthorities().stream()
                .map(Object::toString).toList();

        return new AuthResponse(token, principal.getId(), principal.getFullName(),
                principal.getUsername(), roles);
    }
}
