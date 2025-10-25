package com.example.busconnect.security.config;

import com.example.busconnect.security.config.AuthDtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
//registrarse
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registering new user: {}", request.email());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //login con correo
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt: {}", request.email());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
//login con telefono
    @PostMapping("/login/phone")
    public ResponseEntity<AuthResponse> loginWithPhone(@Valid @RequestBody PhoneLoginRequest request) {
        log.info("Login with phone: {}", request.phone());
        AuthResponse response = authService.loginWithPhone(request);
        return ResponseEntity.ok(response);
    }

    //renovación o refresco de tokens de autenticación.
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh requested");
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    //usuario ya autenticado pueda cambiar su contraseña
    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        String email = authentication.getName();
        MessageResponse response = authService.changePassword(email, request);
        return ResponseEntity.ok(response);
    }

    //validar el token temporal que se envió
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        MessageResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        MessageResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    //toda la info de user logeado
    @GetMapping("/me")
    public ResponseEntity<UserInfo> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Devolver información completa del usuario
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            UserInfo userInfo = new UserInfo(
                    userDetails.getId(),
                    userDetails.getDisplayUsername(),
                    userDetails.getUsername(),
                    userDetails.getPhone(),
                    userDetails.getRole().name(),
                    userDetails.getStatus().name()
            );
            return ResponseEntity.ok(userInfo);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    //verificar la validez y autenticidad de un token de acceso
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(new TokenValidationResponse(false, null, null, null, null));
        }

        String token = authHeader.substring(7);
        TokenValidationResponse response = authService.validateToken(token);
        return ResponseEntity.ok(response);
    }

//cerrar la sesión activa del usuario
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            Authentication authentication,
            @Valid @RequestBody LogoutRequest request
    ) {
        if (authentication != null) {
            log.info("User logged out: {}", authentication.getName());
        }
        MessageResponse response = authService.logout(request);
        return ResponseEntity.ok(response);
    }
}