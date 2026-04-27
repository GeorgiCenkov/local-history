package com.example.localhistory.auth;

import com.example.localhistory.auth.dto.response.AuthResponse;
import com.example.localhistory.auth.request.LoginRequest;
import com.example.localhistory.token.Token;
import com.example.localhistory.token.TokenMapper;
import com.example.localhistory.token.dto.response.TokenDTO;
import com.example.localhistory.token.service.TokenService;
import com.example.localhistory.user.UserRepository;
import com.example.localhistory.user.dto.request.UserCreateRequest;
import com.example.localhistory.user.dto.response.UserDTO;
import com.example.localhistory.user.mapper.UserMapper;
import com.example.localhistory.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final TokenMapper tokenMapper;

    public AuthService(UserRepository userRepository, UserMapper mapper, TokenService tokenService, PasswordEncoder passwordEncoder, TokenMapper tokenMapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.tokenMapper = tokenMapper;
    }

    @Transactional
    public AuthResponse createUser(UserCreateRequest model){
        if(userRepository.findByEmail(model.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with such email already exists.");
        }

        // easier to convert like this not with complicated mappers
        User user;
        switch (model.getRole()){
            case TEACHER -> user = mapper.toTeacher(model);
            case STUDENT -> user = mapper.toStudent(model);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user type.");
        }

        user.setPasswordHash(passwordEncoder.encode(model.getPassword()));

        userRepository.save(user);

        UserDTO userDTO = mapper.toDto(user);
        TokenDTO tokenDTO = tokenService.createToken(user.getId());

        return new AuthResponse(userDTO, tokenDTO);
    }

    @Transactional
    public AuthResponse refreshLogin(String refreshToken){
        Token token = tokenService.refreshToken(refreshToken);
        User user = userRepository.findById(token.getUser().getId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token")
                );

        return new AuthResponse(mapper.toDto(user), tokenMapper.toDTO(token));
    }

    @Transactional
    public AuthResponse login(LoginRequest model){
        User user = userRepository.findByEmail(model.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with such email found."));

        boolean passwordMatches = passwordEncoder.matches(model.getPassword(), user.getPasswordHash());

        if(!passwordMatches){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Supplied password does not match the profile.");
        }

        UserDTO userDTO = mapper.toDto(user);
        TokenDTO tokenDTO = tokenService.createToken(user.getId());

        return new AuthResponse(userDTO, tokenDTO);
    }

}
