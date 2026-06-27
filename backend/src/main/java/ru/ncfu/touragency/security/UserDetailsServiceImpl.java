package ru.ncfu.touragency.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.touragency.foundation.UserRepository;

/**
 * Загружает UserDetails по email (username) для Spring Security.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + email));
    }
}
