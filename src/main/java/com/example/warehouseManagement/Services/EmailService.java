package com.example.warehouseManagement.Services;

public interface EmailService {
    void sendTwoFactorCode(String to, String code);

    void sendPasswordResetLink(String to, String resetUrl);
}
