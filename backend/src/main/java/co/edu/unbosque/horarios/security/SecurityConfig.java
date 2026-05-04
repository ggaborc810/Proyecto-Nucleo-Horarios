package co.edu.unbosque.horarios.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/publico/**").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers(HttpMethod.POST,   "/api/docentes/*/disponibilidad").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.GET,    "/api/docentes/*/disponibilidad").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.PUT,    "/api/docentes/*/disponibilidad/*").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.DELETE, "/api/docentes/*/disponibilidad/*").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.GET, "/api/grupos").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.GET, "/api/grupos/*").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.PUT, "/api/grupos/*/cerrar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/grupos/*/reabrir").hasRole("ADMIN")
                .requestMatchers("/api/auth/**").authenticated()
                .requestMatchers("/api/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> res.setStatus(401))
                .accessDeniedHandler((req, res, e) -> res.setStatus(403))
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
