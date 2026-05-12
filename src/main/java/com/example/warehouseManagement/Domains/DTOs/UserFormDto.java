package com.example.warehouseManagement.Domains.DTOs;

import com.example.warehouseManagement.Domains.User.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFormDto {
    private Long id;

    @NotBlank
    @Size(min = 3, max = 64)
    private String username;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    // Required only on create; ignored on update unless non-blank.
    @Size(min = 8, max = 128)
    private String password;

    @Builder.Default
    private Role role = Role.USER;

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private boolean twoFactorEnabled = false;
}
