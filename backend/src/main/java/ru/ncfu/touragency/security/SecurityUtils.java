package ru.ncfu.touragency.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Статические утилиты для получения данных текущего аутентифицированного
 * пользователя из SecurityContext. Используется в контроллерах (Control-слой).
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    public static Long currentUserId() {
        return getPrincipal().getId();
    }

    public static boolean isAdmin() {
        return getPrincipal().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }

    private static UserDetailsImpl getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        return (UserDetailsImpl) auth.getPrincipal();
    }
}
