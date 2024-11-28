package dev.kiki.donation.auth;

import dev.kiki.donation.auth.jwt.JwtService;
import dev.kiki.donation.user.User;
import dev.kiki.donation.user.UserService;
import dev.kiki.donation.user.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/sign-up")
    public ResponseEntity<RegisterResponseDto> register(
            @Valid @RequestBody RegisterDto registerDto) {

        var newUser = userService.createUser(registerDto);

        UserInfo userInfo = new UserInfo(
                newUser.getId(),
                newUser.getEmail(),
                newUser.getUsername(),
                newUser.getFirstName(),
                newUser.getLastName(),
                newUser.getRole()
        );

        RegisterResponseDto response = new RegisterResponseDto(
                "Registration successful",
                userInfo
        );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login (@Valid @RequestBody LoginDto loginDto) {
        User authenticatedUser = authService.authenticateUser(loginDto);
        String accessToken = jwtService.generateToken(authenticatedUser);

        Map<String, Object> user = Map.of(
                "id", authenticatedUser.getId(),
                "email", authenticatedUser.getEmail(),
                "username", authenticatedUser.getUsername(),
                "role", authenticatedUser.getRole().name()
        );

        LoginResponseDto loginResponse = new LoginResponseDto(
                "Login Successful",
                accessToken,
                user,
                jwtService.expirationTime()
        );

        return ResponseEntity.ok(loginResponse);

    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(name = "Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Invalid token format"));
        }
        String token = authHeader.substring(7);

        jwtService.invalidateToken(token);

        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }


}
