package ru.ncfu.touragency.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.ncfu.touragency.entity.enums.RoleName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("User entity — бизнес-методы")
class UserEntityTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Пётр Петров", "petr@test.com", "encoded_pass", "+79009998877");
    }

    // ─── конструктор ─────────────────────────────────────────────────

    @Test
    @DisplayName("конструктор с параметрами задаёт все поля")
    void constructorSetsAllFields() {
        assertThat(user.getFullName()).isEqualTo("Пётр Петров");
        assertThat(user.getEmail()).isEqualTo("petr@test.com");
        assertThat(user.getPassword()).isEqualTo("encoded_pass");
        assertThat(user.getPhone()).isEqualTo("+79009998877");
        assertThat(user.isActive()).isTrue();
        assertThat(user.getRegisteredAt()).isNotNull();
    }

    @Test
    @DisplayName("по умолчанию ролей нет (пустой Set)")
    void rolesAreEmptyByDefault() {
        assertThat(user.getRoles()).isEmpty();
    }

    // ─── addRole ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("addRole()")
    class AddRole {

        @Test
        @DisplayName("добавляет роль пользователю")
        void addsRole() {
            Role role = new Role(RoleName.ROLE_USER);
            user.addRole(role);
            assertThat(user.getRoles()).containsExactly(role);
        }

        @Test
        @DisplayName("добавление одной роли дважды не дублирует (Set)")
        void doesNotDuplicateRole() {
            Role role = new Role(RoleName.ROLE_USER);
            user.addRole(role);
            user.addRole(role);
            assertThat(user.getRoles()).hasSize(1);
        }
    }

    // ─── isAdmin ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("isAdmin()")
    class IsAdmin {

        @Test
        @DisplayName("возвращает false, если ролей нет")
        void falseWhenNoRoles() {
            assertThat(user.isAdmin()).isFalse();
        }

        @Test
        @DisplayName("возвращает false, если роль ROLE_USER")
        void falseWhenOnlyUserRole() {
            user.addRole(new Role(RoleName.ROLE_USER));
            assertThat(user.isAdmin()).isFalse();
        }

        @Test
        @DisplayName("возвращает true при наличии ROLE_ADMIN")
        void trueWhenAdminRole() {
            user.addRole(new Role(RoleName.ROLE_ADMIN));
            assertThat(user.isAdmin()).isTrue();
        }

        @Test
        @DisplayName("возвращает true при наличии обеих ролей")
        void trueWhenBothRoles() {
            user.addRole(new Role(RoleName.ROLE_USER));
            user.addRole(new Role(RoleName.ROLE_ADMIN));
            assertThat(user.isAdmin()).isTrue();
        }
    }

    // ─── deactivate ───────────────────────────────────────────────────

    @Test
    @DisplayName("deactivate() устанавливает active = false")
    void deactivateSetsActiveFalse() {
        assertThat(user.isActive()).isTrue();
        user.deactivate();
        assertThat(user.isActive()).isFalse();
    }

    // ─── changePassword ───────────────────────────────────────────────

    @Nested
    @DisplayName("changePassword()")
    class ChangePassword {

        @Test
        @DisplayName("устанавливает новый пароль")
        void setsNewPassword() {
            user.changePassword("new_encoded_password");
            assertThat(user.getPassword()).isEqualTo("new_encoded_password");
        }

        @Test
        @DisplayName("бросает IllegalArgumentException для null-пароля")
        void throwsForNullPassword() {
            assertThatThrownBy(() -> user.changePassword(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Пароль не может быть пустым");
        }

        @Test
        @DisplayName("бросает IllegalArgumentException для пустой строки")
        void throwsForEmptyPassword() {
            assertThatThrownBy(() -> user.changePassword(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("бросает IllegalArgumentException для строки из пробелов")
        void throwsForBlankPassword() {
            assertThatThrownBy(() -> user.changePassword("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
