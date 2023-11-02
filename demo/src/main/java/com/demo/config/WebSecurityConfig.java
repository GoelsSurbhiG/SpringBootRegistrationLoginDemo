package com.demo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@SuppressWarnings("ALL")
@Configuration
public class WebSecurityConfig {

    private static final String[] WHITE_LIST_URLS = {

            "/register", "/login",
            "/verifyRegistration*",
            "/resendVerifyToken*", "/updatePassword", "/savePasswordReset*", "/verifyPasswordToken*", "/forgotPassword*"
    };

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

            /*AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

            // Get AuthenticationManager
            AuthenticationManager authenticationManager = authenticationManagerBuilder.build();*/

            http
                    .cors()
                    .and()
                    .csrf()
                    .disable()
                    .authorizeHttpRequests()
                    .requestMatchers(WHITE_LIST_URLS).permitAll();

            return http.build();
        }


}
