package com.clockin.clockin;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class JwtSecretGenerator {
    public static void main(String[] args) {
        // Generate a cryptographically secure key for HS256 algorithm
        Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        String secretString = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("Generated JWT Secret: " + secretString);
    }
}