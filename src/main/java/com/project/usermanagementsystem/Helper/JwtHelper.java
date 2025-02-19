package com.project.usermanagementsystem.Helper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.project.usermanagementsystem.Entities.Role;
import com.project.usermanagementsystem.Entities.User;
import com.project.usermanagementsystem.Filter.JwtAuthenticateFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtHelper {

    private long JWT_VALIDITY_TOKEN = 1000 * 60 * 60;
    private String SECRET_KEY = "mysecret%$h@f#ghfg*&fhgfdxgdfgdggdfgfdgdf";

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticateFilter.class);

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(UserDetails details, List<Role> roles) {
        return Jwts.builder()
                .setSubject(details.getUsername())
                .claim("roles", roles.stream().map(role -> role.getName()).collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_VALIDITY_TOKEN))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        System.out.println("🔹 Extracted JWT Claims: " + claims); // Print all claims for debugging
        return claims.get("roles", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder() // ✅ Use parserBuilder()
                .setSigningKey(getSigningKey())
                .build() // ✅ Must call build()
                .parseClaimsJws(token)
                .getBody();
    }

    // public String generateToken(UserDetails userDetails, String userType) {
    // Map<String, Object> claims = new HashMap<>();
    // claims.put("userType", userType);
    // return createToken(claims, userDetails.getUsername());
    // }

    // private String createToken(Map<String, Object> claims, String subject) {
    // return Jwts.builder()
    // .setClaims(claims)
    // .setSubject(subject)
    // .setIssuedAt(new Date(System.currentTimeMillis()))
    // .setExpiration(new Date(System.currentTimeMillis() + JWT_VALIDITY_TOKEN)) //
    // 24 hours token validity
    // .signWith(getSigningKey(), SignatureAlgorithm.HS256)
    // .compact();
    // }

    // public String extractUserType(String token) {
    // try {
    // Claims claims = extractAllClaims(token);
    // return claims.get("userType", String.class);
    // } catch (Exception e) {
    // log.error("Error extracting user type from token", e);
    // return null;
    // }
    // }

    // // If you don't already have these methods, add them too:
    // private Claims extractAllClaims(String token) {
    // return Jwts.parserBuilder()
    // .setSigningKey(getSigningKey())
    // .build()
    // .parseClaimsJws(token)
    // .getBody();
    // }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, String username) {
        return (extractUsername(token).equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
