package com.example.localhistory.auth;

import com.example.localhistory.auth.dto.response.AuthResponse;
import com.example.localhistory.auth.request.LoginRequest;
import com.example.localhistory.exception.ResourceConflictException;
import com.example.localhistory.token.Token;
import com.example.localhistory.token.TokenMapper;
import com.example.localhistory.token.dto.response.TokenDTO;
import com.example.localhistory.token.service.TokenService;
import com.example.localhistory.user.UserRepository;
import com.example.localhistory.user.dto.request.UserCreateRequest;
import com.example.localhistory.user.dto.response.UserDTO;
import com.example.localhistory.user.mapper.UserMapper;
import com.example.localhistory.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final TokenMapper tokenMapper;

    /**
     * Registers a new user. Throws 409 if the email is already taken, and
     * 400 (IllegalArgumentException) if an unsupported role is supplied.
     */
    @Transactional
    public AuthResponse createUser(UserCreateRequest model) {
        if (userRepository.findByEmail(model.getEmail()).isPresent()) {
            // 409 Conflict — a resource with this identifier already exists
            throw new ResourceConflictException("User with email '" + model.getEmail() + "' already exists");
        }

        User user = switch (model.getRole()) {
            case TEACHER -> mapper.toTeacher(model);
            case STUDENT -> mapper.toStudent(model);
            // IllegalArgumentException → 400 via GlobalExceptionHandler
        };

        user.setPasswordHash(passwordEncoder.encode(model.getPassword()));
        userRepository.save(user);

        UserDTO userDTO = mapper.toDto(user);
        TokenDTO tokenDTO = tokenService.createToken(user.getId(), user.getRole());
        return new AuthResponse(userDTO, tokenDTO);
    }

    /**
     * Issues a new access token using the supplied refresh token.
     * Throws 401 if the refresh token is invalid or the associated user is gone.
     */
    @Transactional
    public AuthResponse refreshLogin(String refreshToken) {
        // TokenService is expected to throw BadCredentialsException on invalid tokens
        Token token = tokenService.refreshToken(refreshToken);

        User user = userRepository.findById(token.getUser().getId())
                // Highly unlikely (user deleted mid-session) but guard it anyway
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        return new AuthResponse(mapper.toDto(user), tokenMapper.toDTO(token));
    }

    /**
     * Authenticates a user by email + password.
     * Throws 404 if no account exists for the email, 401 if the password is wrong.
     * Intentionally distinct error messages — adjust to a single generic message
     * if you prefer not to reveal whether an email is registered.
     */
    @Transactional
    public AuthResponse login(LoginRequest model) {
        // EntityNotFoundException → 404 via GlobalExceptionHandler
        User user = userRepository.findByEmail(model.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("No account found for email: " + model.getEmail()));

        if (!passwordEncoder.matches(model.getPassword(), user.getPasswordHash())) {
            // BadCredentialsException extends AuthenticationException → 401
            throw new BadCredentialsException("Incorrect password");
        }

        UserDTO userDTO = mapper.toDto(user);
        TokenDTO tokenDTO = tokenService.createToken(user.getId(), user.getRole());
        return new AuthResponse(userDTO, tokenDTO);
    }
}