package com.leadmatrix.crm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtility {

   /* private static final SecretKey SECRET_KEY =
            Keys.secretKeyFor(SignatureAlgorithm.HS256);


    //private static String SECRET_KEY = "crmsecretkey";

// Generate token
    public static String generateToken(String email) {

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

// Extract email token validation
    public String extractEmail(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }*/


        private final String SECRET = "secretkey123";

        public String generateToken(String email) {
            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                    .signWith(SignatureAlgorithm.HS256, SECRET)
                    .compact();
        }

        public String extractEmail(String token) {
            return getClaims(token).getSubject();
        }

        public boolean validate(String token) {
            try {
                getClaims(token);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private Claims getClaims(String token) {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        }
    }



