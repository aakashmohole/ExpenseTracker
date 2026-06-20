package com.example.expenseTracker.services;

import com.example.expenseTracker.config.JwtService;
import com.example.expenseTracker.dto.LoginRequest;
import com.example.expenseTracker.dto.LoginResponse;
import com.example.expenseTracker.dto.RegisterRequest;
import com.example.expenseTracker.entity.User;
import com.example.expenseTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
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

        return "User Registered Successfully";
    }


    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("User not found!"));

        boolean match = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!match){
            throw new RuntimeException("Invalid Password!");
        }

        String token =  jwtService.generateToken(user.getEmail());
        return new LoginResponse(token);
    }
}
