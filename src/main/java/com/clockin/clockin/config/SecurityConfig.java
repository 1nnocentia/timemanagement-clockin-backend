package com.clockin.clockin.config;


import com.clockin.clockin.filter.JwtAuthFilter;
import com.clockin.clockin.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

@Configuration
@EnableWebSecurity // Mengaktifkan konfigurasi keamanan web Spring
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    // Konstruktor untuk injeksi dependensi
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

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

    // Mendefinisikan AuthenticationProvider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    // Mendefinisikan AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Konfigurasi rantai filter keamanan HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Menonaktifkan CSRF untuk API RESTful
            .authorizeHttpRequests(authorize -> authorize
    .requestMatchers(
        "/api/signup",
        "/api/login",
        "/api/forgot-password/**",
        "/api/logout", // Izinkan akses ke endpoint logout
        "/swagger-ui.html", // Izinkan akses untuk Swagger UI
        "/swagger-ui/**", // Izinkan akses untuk aset Swagger UI
        "/v3/api-docs/**", // Izinkan akses untuk spesifikasi OpenAPI
        "/webjars/**", // Izinkan akses untuk resource webjars
        "/api/data-jadwal/**", // Izinkan akses untuk endpoint jadwal
        "/api/events/**", // Izinkan akses untuk endpoint events
        "/api/tasks/**", // Izinkan akses untuk endpoint tasks
        "/api/kategori/**", // Izinkan akses untuk endpoint kategori
        "/api/prioritas/**" // Izinkan akses untuk endpoint prioritas
    ).permitAll()
    .anyRequest().authenticated()
)
            // Konfigurasi manajemen sesi agar stateless (tidak menyimpan sesi di server)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Menambahkan AuthenticationProvider kustom
            .authenticationProvider(authenticationProvider())
            // Menambahkan JwtAuthFilter sebelum UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
