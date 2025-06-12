package com.clockin.clockin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    // Anotasi @Bean menandakan bahwa metode ini akan menghasilkan bean yang dikelola oleh Spring IoC container
    // Bean ini adalah BCryptPasswordEncoder yang akan digunakan untuk hashing password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
