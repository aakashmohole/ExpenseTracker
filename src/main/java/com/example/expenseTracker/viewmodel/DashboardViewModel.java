package com.example.expenseTracker.viewmodel;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

@Setter
@Getter
@Component("dashboardViewModel")
@Scope("prototype")
public class DashboardViewModel {
    private String welcomeMessage;
    private String user;

    @Init
    public void init(){
        String token = (String) Sessions.getCurrent()
                .getAttribute("jwtToken");

        if(token == null){
            Executions.sendRedirect("/login.zul");
            return;
        }

        String userE = (String) Sessions.getCurrent()
                .getAttribute("user");

        String msg =(String) Sessions.getCurrent()
                .getAttribute("successMessage");

        if (msg != null) {
            Clients.showNotification(
                    msg,
                    "info",
                    null,
                    "middle_center",
                    3000
            );

            Sessions.getCurrent()
                    .removeAttribute("successMessage");
        }
        user = userE;
        welcomeMessage = "Welcome to Expense Tracker Dashboard!";
    }

    public void logout(){
        Sessions.getCurrent().getAttribute("jwtToken");
        Clients.showNotification(
                "Logged out successfully",
                "info",
                null,
                "middle_center",
                2000
        );

        Executions.sendRedirect("/login.zul");
    }
}
