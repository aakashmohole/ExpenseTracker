package com.example.expenseTracker.viewmodel;

import com.example.expenseTracker.dto.LoginRequest;
import com.example.expenseTracker.dto.LoginResponse;
import com.example.expenseTracker.services.AuthService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;

@Getter
@Setter
@Component("loginViewModel")
@Scope("prototype")
public class LoginViewModel {

    private final AuthService authService;

    private String email;
    private String password;
    private String message;
    private boolean loggedIn;

    public LoginViewModel(AuthService authService) {
        this.authService = authService;
    }

    @Command
    @NotifyChange({"message", "loggedIn", "password"})
    public void login() {
        try {
            LoginRequest request = new LoginRequest();
            request.setEmail(email);
            request.setPassword(password);

            LoginResponse response = authService.login(request);
            Sessions.getCurrent().setAttribute("jwtToken", response.getToken());
            Sessions.getCurrent().setAttribute(
                    "successMessage",
                    "Login Successful!"
            );
            Sessions.getCurrent().setAttribute("user", email);

            Executions.sendRedirect("/dashboard.zul");

            password = null;
            loggedIn = true;
            message = "Login successful";
        } catch (RuntimeException ex) {
            loggedIn = false;
            message = ex.getMessage();
        }
    }
}
