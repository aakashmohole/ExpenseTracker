package com.example.expenseTracker.viewmodel;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

@Setter
@Getter
@Component("dashboardViewModel")
@Scope("prototype")
public class DashboardViewModel {
    private String welcomeMessage;

    @Init
    public void init(){
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

        welcomeMessage = "Welcome to Expense Tracker Dashboard!";
    }
}
