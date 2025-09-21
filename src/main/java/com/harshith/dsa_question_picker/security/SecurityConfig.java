package com.harshith.dsa_question_picker.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2Service oAuth2Service;
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String token = tokenProvider.createToken(authentication);
            long tokenExpirySeconds = 7 * 24 * 60 * 60;

            // Create a secure HttpOnly cookie
            String cookie = String.format(
                    "auth_token=%s; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=%d",
                    token, tokenExpirySeconds
            );

            response.setHeader("Set-Cookie", cookie);
            response.sendRedirect(frontendUrl + "/dashboard");
        };
    }


    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {
                })
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/public/**", "/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuth2AuthenticationSuccessHandler())
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(oAuth2Service)
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            response.setHeader("Set-Cookie",
                                    "auth_token=; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=0;");
                        })
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\":\"Logged out\"}");
                        })
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                );
        http.addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
