package com.example.busconnect.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.busconnect.security.error.Http401EntryPoint;
import com.example.busconnect.security.error.Http403AccessDenied;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final Http401EntryPoint http401EntryPoint;
    private final Http403AccessDenied http403AccessDenied;

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:4200}")
    private String allowedOrigins;

    @Value("${app.security.enable-csrf:false}")
    private boolean enableCsrf;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**",
            "/api/v1/routes/search",
            "/api/v1/trips/search",
            "/api/v1/stops/search",
            "/actuator/health",
            "/actuator/info",
            "/api-docs/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(http401EntryPoint)
                        .accessDeniedHandler(http403AccessDenied)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Públicos
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/api/v1/routes/*/stops").permitAll()

                        // Actuator solo para ADMIN
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        //  Lectura para todos autenticados, escritura solo admin/dispatcher
                        .requestMatchers(HttpMethod.GET, "/api/v1/routes/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/routes/**").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/routes/**").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/routes/**").hasRole("ADMIN")

                        // Lectura para todos autenticados, escritura solo admin/dispatcher
                        .requestMatchers(HttpMethod.GET, "/api/v1/trips/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/trips/**").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/trips/**").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/trips/**").hasRole("ADMIN")

                        // Cualquier autenticado (@PreAuthorize en controllers maneja permisos específicos)
                        .requestMatchers("/api/v1/tickets/**").authenticated()
                        
                        //  BAGGAGE, PARCELS, SEAT-HOLDS - Cualquier autenticado
                        .requestMatchers("/api/v1/baggage/**").authenticated()
                        .requestMatchers("/api/v1/parcels/**").authenticated()
                        .requestMatchers("/api/v1/seat-holds/**").authenticated()

                        // Driver endpoints - patrones específicos
                        .requestMatchers("/api/v1/trips/*/depart").hasAnyRole("DRIVER", "ADMIN", "DISPATCHER")
                        .requestMatchers("/api/v1/trips/*/boarding/**").hasAnyRole("DRIVER", "ADMIN", "DISPATCHER")
                        .requestMatchers("/api/v1/parcels/*/delivered").hasAnyRole("DRIVER", "ADMIN", "DISPATCHER")
                        .requestMatchers("/api/v1/tickets/*/used").hasAnyRole("DRIVER", "ADMIN", "DISPATCHER")
                        .requestMatchers("/api/v1/tickets/*/no-show").hasAnyRole("DRIVER", "ADMIN", "DISPATCHER")
                        .requestMatchers("/api/v1/trips/driver/**").hasAnyRole("DRIVER", "ADMIN", "DISPATCHER")

                        // Dispatcher endpoints
                        .requestMatchers("/api/v1/assignments/**").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers("/api/v1/trips/*/assign").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers("/api/v1/buses/**").hasAnyRole("DISPATCHER", "ADMIN")
                        .requestMatchers("/api/v1/overbooking/**").hasAnyRole("DISPATCHER", "ADMIN", "CLERK")

                        // Resto de endpoints requieren autenticación
                        .requestMatchers("/api/v1/**").authenticated()
                        .requestMatchers("/test/**").permitAll()

                        .anyRequest().authenticated()
                )
                .userDetailsService(customUserDetailsService)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}