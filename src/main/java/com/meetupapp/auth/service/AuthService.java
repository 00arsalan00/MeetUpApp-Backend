package com.meetupapp.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.meetupapp.auth.dto.LoginRequest;
import com.meetupapp.auth.dto.LoginResponse;
import com.meetupapp.auth.dto.RegisterRequest;
import com.meetupapp.auth.dto.RegisterResponse;
import com.meetupapp.auth.entity.User;
import com.meetupapp.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // REGISTER
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already in use.");
        }

        String hashed = passwordEncoder.encode(request.password());

        User user = User.builder()
                .email(request.email())
                .passwordHash(hashed)
                .displayName(request.displayName())
                .build();

        userRepository.save(user);

        return new RegisterResponse(
                user.getUserId(),
                user.getEmail(),
                user.getDisplayName(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }

    // LOGIN
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid Credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid Credentials");
        }

        String token = jwtService.generateToken(user.getUserId(), user.getEmail());

        return new LoginResponse(
                token,
                (int) jwtService.getExpirationSeconds(),
                user.getUserId(),
                user.getEmail()
        );
    }
}
