package ru.ncfu.touragency.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ncfu.touragency.entity.Role;
import ru.ncfu.touragency.entity.enums.RoleName;

import java.util.Optional;

/**
 * Foundation-слой (PCMEF) — доступ к данным. Spring Data JPA генерирует
 * реализацию интерфейса автоматически (proxy), что эквивалентно ручной
 * паре IRepository/RepositoryImpl, описанной в методичке.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
