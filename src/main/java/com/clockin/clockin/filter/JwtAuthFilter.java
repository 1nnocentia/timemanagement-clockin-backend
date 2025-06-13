package com.clockin.clockin.filter;

import com.clockin.clockin.config.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Anotasi @Component menandakan kelas ini adalah komponen Spring
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService; // Menggunakan UserDetailsService yang sudah kita buat

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Periksa apakah header Authorization ada dan berformat "Bearer <token>"
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Ambil token JWT setelah "Bearer "
            try {
                username = jwtUtil.extractUsername(jwt); // Ekstrak username dari token
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                logger.warn("JWT Token sudah kadaluarsa", e);
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                logger.warn("JWT Token tidak valid", e);
            } catch (io.jsonwebtoken.SignatureException e) {
                logger.warn("Tanda tangan JWT tidak valid", e);
            } catch (Exception e) {
                logger.error("Error saat mengurai JWT Token", e);
            }
        }

        // Jika username ditemukan dan belum ada autentikasi di SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validasi token dengan userDetails
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Buat objek autentikasi dan set di SecurityContext
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // Lanjutkan rantai filter
        filterChain.doFilter(request, response);
    }
}