package com.harshith.dsa_question_picker.security;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class SessionConfig {

    private static final Duration SESSION_TIMEOUT = Duration.ofDays(7);

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            // Server-side session timeout
            servletContext.setSessionTimeout((int) SESSION_TIMEOUT.toMinutes());

            // Configure the JSESSIONID cookie
            var cookieConfig = servletContext.getSessionCookieConfig();
            cookieConfig.setMaxAge((int) SESSION_TIMEOUT.getSeconds()); // persist cookie for 7 days
            cookieConfig.setHttpOnly(false);
            cookieConfig.setName("JSESSIONID");
        };
    }
}
