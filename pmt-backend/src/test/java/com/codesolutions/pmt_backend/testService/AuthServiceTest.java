package com.codesolutions.pmt_backend.testService;

import com.codesolutions.pmt_backend.entity.User;
import com.codesolutions.pmt_backend.repository.UserRepository;
import com.codesolutions.pmt_backend.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("register: sauvegarde l'utilisateur via le repository")
    void register_callsSaveAndReturnsUser() {
        User input = new User();
        input.setEmail("test@example.com");
        input.setPassword("pwd");
        User saved = new User();
        saved.setId(1L);
        saved.setEmail("test@example.com");
        saved.setPassword("pwd");

        when(userRepository.save(input)).thenReturn(saved);

        User out = authService.register(input);

        assertThat(out).isSameAs(saved);
        verify(userRepository).save(input);
    }

    @Test
    @DisplayName("login: utilisateur trouvé → Optional.of(user)")
    void login_userFound_returnsOptional() {
        String email = "foo@bar.com";
        String password = "secret";

        User u = new User();
        u.setId(2L);
        u.setEmail(email);
        u.setPassword("hashed"); // on ne vérifie pas ici

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(u));

        Optional<User> result = authService.login(email, password);

        assertThat(result).isPresent()
                .contains(u);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("login: utilisateur non trouvé → Optional.empty()")
    void login_userNotFound_returnsEmpty() {
        String email = "nouser@x.com";
        String password = "nopwd";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = authService.login(email, password);

        assertThat(result).isEmpty();
        verify(userRepository).findByEmail(email);
    }
}
