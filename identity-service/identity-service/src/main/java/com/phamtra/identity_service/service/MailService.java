package com.phamtra.identity_service.service;

public interface MailService {
    void sendVerificationEmail(String email, String token, Long userId);
    void sendPasswordResetEmail(String email, String token);
}
