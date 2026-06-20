package com.example.expenseTracker.services;

import com.example.expenseTracker.config.JwtService;
import com.example.expenseTracker.dto.LoginRequest;
import com.example.expenseTracker.dto.LoginResponse;
import com.example.expenseTracker.dto.RegisterRequest;
import com.example.expenseTracker.entity.User;
import com.example.expenseTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public String register(RegisterRequest request){
        logger.info("Registration request received for email: {}", request.getEmail());

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            logger.warn("Registration failed. Email already exists: {}", request.getEmail());
            throw new RuntimeException("Email already exist!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole("ROLE_USER");

        userRepository.save(user);

        logger.info("User registered successfully with email: {}", user.getEmail());

        return "User Registered Successfully";
    }


    public LoginResponse login(LoginRequest request) {
        logger.info("Login request received for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("User not found!"));

        boolean match = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!match){
            logger.warn("Invalid password for email: {}", request.getEmail());
            throw new RuntimeException("Invalid Password!");
        }

        String token =  jwtService.generateToken(user.getEmail());
        logger.info("Token generated for user: {}", user.getEmail());
        return new LoginResponse(token);
    }
}
