package com.clockin.clockin.config;


// import com.clockin.filter.JwtAuthFilter;
import com.clockin.clockin.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

@Configuration
@EnableWebSecurity // Mengaktifkan konfigurasi keamanan web Spring
public class SecurityConfig {

    // Konstruktor untuk injeksi dependensi
    // private final UserDetailsServiceImpl userDetailsService;
    // public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
    //     this.userDetailsService = userDetailsService;
    // }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttlsEnable;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.debug", "true"); // Atur ke true untuk debugging lebih lanjut

        return mailSender;
    }

    // Konfigurasi rantai filter keamanan HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Menonaktifkan CSRF untuk API RESTful
            .authorizeHttpRequests(authorize -> authorize
                // Mengizinkan akses tanpa otentikasi ke endpoint-endpoint ini
                .requestMatchers(
                    "/api/signup",
                    "/api/login",
                    "/api/forgot-password/**",
                    // ** BARU: Izinkan akses untuk Swagger UI dan OpenAPI Docs **
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/webjars/**" // Diperlukan untuk resource webjars (misalnya, font, CSS)
                ).permitAll()
                // Membutuhkan otentikasi untuk semua permintaan lainnya
                .anyRequest().authenticated()
            )
            .formLogin(AbstractHttpConfigurer::disable) // Menonaktifkan form login default Spring Security jika tidak diperlukan
            .httpBasic(AbstractHttpConfigurer::disable); // Menonaktifkan basic HTTP authentication jika tidak diperlukan

        return http.build();
    }
}