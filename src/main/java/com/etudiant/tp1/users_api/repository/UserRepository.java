package com.etudiant.tp1.users_api.repository;

import com.etudiant.tp1.users_api.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Recherche un utilisateur par son email, retourne un Optional<User>
    Optional<User> findByEmail(String email);
}