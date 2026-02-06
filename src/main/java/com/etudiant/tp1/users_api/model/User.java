package com.etudiant.tp1.users_api.model;

import jakarta.persistence.*;

// Entité représentant un utilisateur dans la base de données
@Entity
@Table(name = "users")
public class User {
    // Identifiant unique généré automatiquement (clé primaire)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom de l'utilisateur
    private String name;

    // Email de l'utilisateur, doit être unique dans la base
    @Column(unique = true)
    private String email;

    // Mot de passe de l'utilisateur (stocké hashé)
    private String password;

    // Champ pour le rôle (USER ou ADMIN)
    @Column(nullable = false)
    private String role = "USER"; // Valeur par défaut : USER

    // Constructeur par défaut requis par JPA
    public User() {
    }

    // Constructeur avec paramètres pour initialiser un utilisateur
    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = (role != null && !role.isEmpty()) ? role : "USER";
    }

    // Getter pour l'id
    public Long getId() {
        return id;
    }

    // Setter pour l'id
    public void setId(Long id) {
        this.id = id;
    }

    // Getter pour le nom
    public String getName() {
        return name;
    }

    // Setter pour le nom
    public void setName(String name) {
        this.name = name;
    }

    // Getter pour l'email
    public String getEmail() {
        return email;
    }

    // Setter pour l'email
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter pour le mot de passe
    public String getPassword() {
        return password;
    }

    // Setter pour le mot de passe
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter pour le rôle
    public String getRole() {
        return role;
    }

    // Setter pour le rôle
    public void setRole(String role) {
        this.role = role;
    }
}