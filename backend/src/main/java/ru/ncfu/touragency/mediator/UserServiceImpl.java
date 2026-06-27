package ru.ncfu.touragency.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.touragency.dto.request.UpdateProfileRequest;
import ru.ncfu.touragency.dto.response.UserResponse;
import ru.ncfu.touragency.entity.User;
import ru.ncfu.touragency.exception.ResourceNotFoundException;
import ru.ncfu.touragency.foundation.UserRepository;

import java.util.List;

/**
 * Mediator-слой (PCMEF) — управление профилями пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long id, UpdateProfileRequest request) {
        User user = findOrThrow(id);
        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        return toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        User user = findOrThrow(id);
        user.deactivate();
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден, id=" + id));
    }

    private UserResponse toResponse(User u) {
        List<String> roles = u.getRoles().stream()
                .map(r -> r.getName().name())
                .toList();
        return new UserResponse(u.getId(), u.getFullName(), u.getEmail(),
                u.getPhone(), u.isActive(), u.getRegisteredAt(), roles);
    }
}
