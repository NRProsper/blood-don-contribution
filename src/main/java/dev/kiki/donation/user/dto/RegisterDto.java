package dev.kiki.donation.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "First name is required")
        String lastName,

        @NotBlank(message = "First name is required")
        @Size(min = 5, max = 20, message = "Username must be between 3 and 20 long")
        String userName,

        @Email(message = "Enter a valid email")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
) {}
