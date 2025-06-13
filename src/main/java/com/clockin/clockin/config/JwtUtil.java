package com.clockin.clockin.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

// Anotasi @Component menandakan kelas ini adalah komponen Spring dan dapat di-autowire
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}") // Mengambil nilai secret key dari application.properties
    private String secret;

    @Value("${jwt.expirationMs}") // Mengambil nilai masa berlaku token dari application.properties
    private int jwtExpirationMs;

    // // Mendapatkan kunci signing dari secret key yang telah di-decode Base64
    // private Key getSigningKey() {
    //     byte[] keyBytes = Decoders.BASE64.decode(secret);
    //     return Keys.hmacShaKeyFor(keyBytes);
    // }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // // Mendapatkan semua klaim dari token
    // public Claims extractAllClaims(String token) {
    //     return Jwts.parserBuilder()
    //             .setSigningKey(getSigningKey())
    //             .build()
    //             .parseClaimsJws(token)
    //             .getBody();
    // }

    public Claims extractAllClaims(String token) {
        JwtParser parser = Jwts.parser().verifyWith(getSigningKey()).build();
        return parser.parseSignedClaims(token).getPayload();
    }


    // Mendapatkan satu klaim spesifik dari token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Mendapatkan username dari token (subjek)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Mendapatkan tanggal kadaluarsa dari token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Memeriksa apakah token sudah kadaluarsa
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Memvalidasi token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Membuat token JWT
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    // Metode internal untuk membangun token JWT
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}