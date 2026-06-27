package ru.ncfu.touragency.mediator;

import ru.ncfu.touragency.dto.request.UpdateProfileRequest;
import ru.ncfu.touragency.dto.response.UserResponse;

import java.util.List;

public interface IUserService {
    UserResponse getById(Long id);
    UserResponse updateProfile(Long id, UpdateProfileRequest request);
    List<UserResponse> getAll();        // только администратор
    void deactivate(Long id);           // только администратор
}
