package com.example.expenseTracker.config;

import com.example.expenseTracker.entity.Transaction;
import com.example.expenseTracker.entity.User;
import com.example.expenseTracker.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    private final UserService userService;
    @Value("${jwt.secret}")
    private String secretKey;

    public JwtService(UserService userService) {
        this.userService = userService;
    }

    public String generateToken(String email){
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 86400000
                        )
                )
                .signWith(
                        Keys.hmacShaKeyFor(
                                secretKey.getBytes()
                        )
                )
                .compact();
    }

    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(
                        Keys.hmacShaKeyFor(
                                secretKey.getBytes()
                        )
                )
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        String username =
                extractUsername(token);
        return username.equals(
                userDetails.getUsername());
    }

}
