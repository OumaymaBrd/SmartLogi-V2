package org.example.smartspring.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.smartspring.entities.Permission;
import org.example.smartspring.security.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtServiceExtended {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();

        // CORRECTION 1 : Accès au nom du rôle via l'entité Role
        // On vérifie si le rôle existe pour éviter un NullPointerException
        String roleName = (user.getRole() != null) ? user.getRole().getName() : "USER";
        extraClaims.put("role", roleName);

        // CORRECTION 2 : Extraction des permissions (Set<Permission> dans User)
        String permissions = user.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.joining(","));
        extraClaims.put("permissions", permissions);

        extraClaims.put("userId", user.getId());

        // CORRECTION 3 : Syntaxe JJWT 0.12+ (Utilisation de claims() et signWith())
        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey()) // Signature automatique avec l'algorithme sécurisé
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        // CORRECTION 4 : Syntaxe de parsing moderne pour JJWT 0.12+
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}