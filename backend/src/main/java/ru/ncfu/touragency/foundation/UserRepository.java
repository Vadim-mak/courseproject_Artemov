package ru.ncfu.touragency.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ncfu.touragency.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
