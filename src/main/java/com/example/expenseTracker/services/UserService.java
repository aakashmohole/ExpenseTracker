package com.example.expenseTracker.services;

import com.example.expenseTracker.entity.User;
import com.example.expenseTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser(){
        String email = getCurrentEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

    }

    public String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            return authentication.getName();
        }

        Session session = Sessions.getCurrent(false);
        if (session != null) {
            Object user = session.getAttribute("user");
            if (user instanceof String email && !email.isBlank()) {
                return email;
            }
        }

        throw new RuntimeException("User not found!");
    }
}
