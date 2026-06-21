package com.example.expenseTracker.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;

import java.util.Map;

@Configuration
public class ZkConfig {

    @Bean
    public ServletRegistrationBean<DHtmlLayoutServlet> zkLoader() {
        ServletRegistrationBean<DHtmlLayoutServlet> registration =
                new ServletRegistrationBean<>(new DHtmlLayoutServlet(), "*.zul");
        registration.setLoadOnStartup(1);
        registration.setInitParameters(Map.of(
                "update-uri", "/zkau",
                "compress", "false"
        ));
        return registration;
    }

    @Bean
    public ServletRegistrationBean<DHtmlUpdateServlet> zkAuEngine() {
        ServletRegistrationBean<DHtmlUpdateServlet> registration =
                new ServletRegistrationBean<>(new DHtmlUpdateServlet(), "/zkau/*");
        registration.setLoadOnStartup(2);
        return registration;
    }
}
