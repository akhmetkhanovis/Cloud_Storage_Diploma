package ru.netology.cloudstorage.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Service
@AllArgsConstructor
public class JwtTokenProvider implements Serializable {
    @Serial
    private static final long serialVersionUID = -4412363932038316680L;

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    public Date getExpirationDate(String token) {
        return parseToken(token).getExpiration();
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim(jwtConfig.getClaimsMapKey(), userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
                .signWith(secretKey)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = parseToken(token).getExpiration();
        return expiration.before(new Date());
    }
}
