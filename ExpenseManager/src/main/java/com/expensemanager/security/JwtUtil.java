package com.expensemanager.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.expensemanager.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtil {


    private final String SECRET =
            "expensemanagersecretkeyexpensemanagersecretkey12345";


    private final long EXPIRATION_TIME = 1000 * 60 * 60;


    private SecretKey getKey() {

        return Keys.hmacShaKeyFor(
                SECRET.getBytes()
        );
    }


    public String generateToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                            System.currentTimeMillis()
                            + EXPIRATION_TIME
                        )
                )
                .signWith(getKey())
                .compact();
    }


    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public Long extractUserId(String token) {

        return extractClaims(token)
                .get("userId", Long.class);
    }


    public boolean validateToken(String token) {

        try {

            extractClaims(token);
            return true;

        } catch(Exception e) {

            return false;
        }
    }
}