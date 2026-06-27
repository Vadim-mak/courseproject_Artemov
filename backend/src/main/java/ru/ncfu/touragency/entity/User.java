package ru.ncfu.touragency.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность Entity-слоя (PCMEF). Пользователь системы (турист или администратор).
 * Содержит не только состояние, но и бизнес-методы (требование методички —
 * избегать анемичной модели).
 */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User(String fullName, String email, String password, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    /* ---------- бизнес-методы (Entity-слой не должен быть анемичным) ---------- */

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public boolean isAdmin() {
        return roles.stream().anyMatch(r -> r.getName().name().equals("ROLE_ADMIN"));
    }

    public void deactivate() {
        this.active = false;
    }

    public void changePassword(String newEncodedPassword) {
        if (newEncodedPassword == null || newEncodedPassword.isBlank()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }
        this.password = newEncodedPassword;
    }
}
