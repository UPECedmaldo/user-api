package com.etudiant.tp1.users_api.controller;

import com.etudiant.tp1.users_api.model.User;
import com.etudiant.tp1.users_api.repository.UserRepository;
import com.etudiant.tp1.users_api.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    // Teste la création d'un utilisateur avec rôle par défaut (POST /users)
    @Test
    void createUser_shouldReturn201_withDefaultRole() throws Exception {
        User user = new User("Test", "test@example.com", "password", "USER");
        user.setId(1L);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("hashed");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        String body = "{\"name\":\"Test\",\"email\":\"test@example.com\",\"password\":\"password\"}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    // Teste la création d'un utilisateur avec rôle ADMIN
    @Test
    void createUser_shouldReturn201_withAdminRole() throws Exception {
        User user = new User("Admin", "admin@example.com", "password", "ADMIN");
        user.setId(2L);
        user.setRole("ADMIN");
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("hashed");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        String body = "{\"name\":\"Admin\",\"email\":\"admin@example.com\",\"password\":\"password\",\"role\":\"ADMIN\"}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    // Teste la récupération de tous les utilisateurs (GET /users)
    @Test
    void getAllUsers_shouldReturn200() throws Exception {
        User user1 = new User("Test1", "test1@example.com", "password", "USER");
        User user2 = new User("Test2", "test2@example.com", "password", "USER");
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("USER"))
                .andExpect(jsonPath("$[1].role").value("USER"));
    }

    // Teste la récupération d'un utilisateur par ID existant (GET /users/1)
    @Test
    void getUserById_shouldReturn200() throws Exception {
        User user = new User("Test", "test@example.com", "password", "USER");
        user.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    // Teste la récupération d'un utilisateur inexistant (GET /users/99)
    @Test
    void getUserById_shouldReturn404() throws Exception {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    // Teste la modification d'un utilisateur existant (PUT /users/1)
    @Test
    void updateUser_shouldReturn200() throws Exception {
        User existing = new User("Old", "old@example.com", "oldpass", "USER");
        existing.setId(1L);
        User updated = new User("New", "new@example.com", "newpass", "USER");
        updated.setId(1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("hashed");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updated);

        String body = "{\"name\":\"New\",\"email\":\"new@example.com\",\"password\":\"newpass\"}";

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    // Teste la modification du rôle d'un utilisateur
    @Test
    void updateUser_shouldUpdateRole() throws Exception {
        User existing = new User("Test", "test@example.com", "password", "USER");
        existing.setId(1L);
        User updated = new User("Test", "test@example.com", "password", "ADMIN");
        updated.setId(1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updated);

        String body = "{\"name\":\"Test\",\"email\":\"test@example.com\",\"role\":\"ADMIN\"}";

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    // Teste la modification d'un utilisateur inexistant (PUT /users/99)
    @Test
    void updateUser_shouldReturn404() throws Exception {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        String body = "{\"name\":\"New\",\"email\":\"new@example.com\",\"password\":\"newpass\"}";

        mockMvc.perform(put("/users/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound());
    }

    // Teste la suppression d'un utilisateur existant (DELETE /users/1)
    @Test
    void deleteUser_shouldReturn200() throws Exception {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    // Teste la suppression d'un utilisateur inexistant (DELETE /users/99)
    @Test
    void deleteUser_shouldReturn404() throws Exception {
        Mockito.when(userRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());
    }

    // Teste que le mot de passe est bien hashé lors de la création
    @Test
    void createUser_shouldHashPassword() throws Exception {
        User user = new User("Test", "test@example.com", "hashed", "USER");
        user.setId(1L);

        Mockito.when(passwordEncoder.encode("password")).thenReturn("hashed");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        String body = "{\"name\":\"Test\",\"email\":\"test@example.com\",\"password\":\"password\"}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").value("hashed"));

        Mockito.verify(passwordEncoder).encode("password");
    }

    // Teste que le rôle par défaut est bien USER si non spécifié
    @Test
    void createUser_shouldSetDefaultRoleIfNotProvided() throws Exception {
        User user = new User("Test", "test@example.com", "password", "USER");
        user.setId(1L);

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("hashed");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            if (savedUser.getRole() == null || savedUser.getRole().isEmpty()) {
                savedUser.setRole("USER");
            }
            return savedUser;
        });

        String body = "{\"name\":\"Test\",\"email\":\"test@example.com\",\"password\":\"password\"}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }
}