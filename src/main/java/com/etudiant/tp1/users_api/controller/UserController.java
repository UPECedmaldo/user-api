package com.etudiant.tp1.users_api.controller;

import com.etudiant.tp1.users_api.model.User;
import com.etudiant.tp1.users_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    // Récupère tous les utilisateurs (nécessite rôle USER ou ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    // Crée un nouvel utilisateur (public)
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        // Par défaut, les nouveaux utilisateurs ont le rôle USER
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        User savedUser = repository.save(user);
        return ResponseEntity.status(201).body(savedUser);
    }

    // Récupère un utilisateur par son ID (nécessite rôle USER ou ADMIN)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return repository.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    // Modifie un utilisateur existant (nécessite rôle ADMIN uniquement)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User newUser) {
        return repository.findById(id)
                .map(user -> {
                    user.setName(newUser.getName());
                    user.setEmail(newUser.getEmail());
                    if (newUser.getPassword() != null && !newUser.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
                    }
                    // Permet de modifier le rôle uniquement si fourni
                    if (newUser.getRole() != null && !newUser.getRole().isEmpty()) {
                        user.setRole(newUser.getRole());
                    }
                    return ResponseEntity.ok(repository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Supprime un utilisateur par son ID (nécessite rôle ADMIN uniquement)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
