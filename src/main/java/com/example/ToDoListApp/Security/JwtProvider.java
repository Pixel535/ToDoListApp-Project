package com.example.ToDoListApp.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JwtProvider {
    static final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    public static String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);

        String jwt = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 86400000))
                .claim("username", auth.getName())
                .claim("authorities", roles)
                .signWith(key)
                .compact();

        System.out.println("Token generated in JwtProvider: " + jwt);
        return jwt;
    }

    private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }

    public static String getUsernameFromJwtToken(String jwt) {
        jwt = jwt.substring(7);
        try {
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
            String username = String.valueOf(claims.get("username"));
            System.out.println("Username extracted from JWT: " + claims);
            return username;
        } catch (Exception e) {
            System.err.println("Error extracting username from JWT: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
