package com.etudiant.tp1.users_api.controller;

import com.etudiant.tp1.users_api.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    // Endpoint pour se connecter et obtenir un JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Authentifie l'utilisateur avec le nom d'utilisateur et le mot de passe
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // Récupère les détails de l'utilisateur authentifié
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Génère un token JWT pour l'utilisateur
        String jwt = jwtService.generateToken(userDetails.getUsername());

        // Retourne le token JWT dans la réponse
        return ResponseEntity.ok(new LoginResponse(jwt));
    }
}

// Représente la requête de connexion (login)
class LoginRequest {
    private String username;
    private String password;

    // Getters et setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

// Représente la réponse envoyée après une connexion réussie (contenant le token
// JWT)
class LoginResponse {
    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
