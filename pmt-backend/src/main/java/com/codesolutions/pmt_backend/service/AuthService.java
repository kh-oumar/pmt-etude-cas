package com.codesolutions.pmt_backend.service;

import com.codesolutions.pmt_backend.entity.User;
import com.codesolutions.pmt_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        // Ici tu peux ajouter la logique de validation et de hashage du mot de passe
        return userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        // Ici, implémente la logique de vérification (par exemple, comparer le mot de passe hashé)
        return userRepository.findByEmail(email);
    }
}

