package com.etudiant.tp1.users_api.controller;

import com.etudiant.tp1.users_api.security.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// Test unitaire du contrôleur d'authentification (AuthController)
@WebMvcTest(AuthController.class)
@Import(com.etudiant.tp1.users_api.config.TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc; // Permet de simuler des requêtes HTTP sur le contrôleur

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    // Teste que la route /auth/login retourne bien un token JWT
    @Test
    void login_shouldReturnToken() throws Exception {
        String email = "test@example.com";
        String password = "password";
        String token = "jwt-token";

        // Mock de l'objet Authentication retourné par l'AuthenticationManager
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(
                org.springframework.security.core.userdetails.User
                        .withUsername(email).password(password)
                        .authorities(new org.springframework.security.core.GrantedAuthority[0]).build());
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(jwtService.generateToken(email)).thenReturn(token);

        // Corps de la requête JSON pour le login
        String body = "{\"username\":\"" + email + "\",\"password\":\"" + password + "\"}";

        // Effectue une requête POST sur /auth/login et vérifie le statut et le contenu
        // du token
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }
}
