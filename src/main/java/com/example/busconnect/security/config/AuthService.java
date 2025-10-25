package com.example.busconnect.security.config;

import com.example.busconnect.security.config.AuthDtos.*;
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse loginWithPhone(PhoneLoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    AuthResponse generateOfflineToken(String userEmail);
    MessageResponse changePassword(String userEmail, ChangePasswordRequest request);

    MessageResponse forgotPassword(ForgotPasswordRequest request);
    MessageResponse resetPassword(ResetPasswordRequest request);

    MessageResponse logout(LogoutRequest request);
    TokenValidationResponse validateToken(String token);
}
