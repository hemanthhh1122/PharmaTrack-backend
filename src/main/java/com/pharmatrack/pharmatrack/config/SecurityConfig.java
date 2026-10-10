package com.pharmatrack.pharmatrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "https://pharma-track-frontend-gamma.vercel.app"
                ));
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(
                List.of("Content-Type", "X-XSRF-TOKEN", "X-CSRF-TOKEN"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/register"
                ).permitAll()

                .requestMatchers(
                    HttpMethod.DELETE, "/api/medicines/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET, "/api/medicines/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(
                    HttpMethod.POST, "/api/medicines/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(
                    HttpMethod.PUT, "/api/medicines/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(
                    HttpMethod.DELETE, "/api/suppliers/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET, "/api/suppliers/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(
                    HttpMethod.POST, "/api/suppliers/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(
                    HttpMethod.PUT, "/api/suppliers/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(
                    HttpMethod.DELETE, "/api/batches/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET, "/api/batches/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(
                    HttpMethod.POST, "/api/batches/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(
                    HttpMethod.DELETE, "/api/sales/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET, "/api/sales/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(
                    HttpMethod.POST, "/api/sales/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .anyRequest().authenticated()
            );

        return http.build();
    }
}