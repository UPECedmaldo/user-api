package com.etudiant.tp1.users_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Filtre qui s'exécute à chaque requête pour vérifier la présence et la validité du JWT
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    // Méthode principale du filtre, appelée à chaque requête HTTP
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Récupère l'en-tête Authorization de la requête
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Si l'en-tête est absent ou ne commence pas par "Bearer ", on passe au filtre
        // suivant
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrait le token JWT de l'en-tête
        jwt = authHeader.substring(7);
        // Extrait l'email (username) à partir du JWT
        userEmail = jwtService.extractUsername(jwt);

        // Si l'email est présent et qu'aucune authentification n'est déjà définie dans
        // le contexte
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Charge les détails de l'utilisateur
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Si le token est expiré, on passe au filtre suivant sans authentifier
                if (jwtService.isTokenExpired(jwt)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                // Crée un objet d'authentification et le place dans le contexte de sécurité
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                // Si l'utilisateur n'existe plus, on continue sans authentifier
                // Le token est invalide mais on ne bloque pas la requête
            }
        }
        // Passe au filtre suivant dans la chaîne
        filterChain.doFilter(request, response);
    }
}
