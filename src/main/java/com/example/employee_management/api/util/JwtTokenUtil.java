package com.example.employee_management.api.util;

import com.example.employee_management.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.example.employee_management.constants.ApplicationConstants.JWT_TOKEN_VALIDITY;

@Component
@Slf4j
public class JwtTokenUtil {

    private final Environment env;
    private Key key;

    @Autowired
    public JwtTokenUtil(Environment env) {
        this.env = env;
        this.key = getSigningKey();
        log.info("JWT Secret initialized");
    }

    private Key getSigningKey() {
        String secret = env.getProperty("jwt.secret");
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret is not set. Please check your application.properties or application.yml file.");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userName, Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRoles());
        return doGenerateToken(claims, userName, "access");
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, String tokenType) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = tokenType.equals("access") ? new Date(now + JWT_TOKEN_VALIDITY * 1000) : null;

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(issuedAt)
                    .setExpiration(expiration)
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token: {}", e.getMessage());
            throw new IllegalStateException("Failed to generate JWT token. Please check your JWT configuration.", e);
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration != null && expiration.before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}