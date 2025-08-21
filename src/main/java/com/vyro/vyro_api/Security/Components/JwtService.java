package com.vyro.vyro_api.Security.Components;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.vyro.vyro_api.AppUsers.Database.Enums.EnumUserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

@Service
public class JwtService {

    // ========================= Initialization =========================
    private final String secretKey;

    @Getter
    private final int expiration = (1000 * 60) * 5;

    // Constructor
    public JwtService(@Value("${app.secretKey}") String secretKey) {
        this.secretKey = secretKey;
    }

    // ========================= Extracts Logic =========================
    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public EnumUserRole extractRoles(String jwtToken) throws NotFoundException {

        EnumUserRole role = this.<EnumUserRole>extractClaim(jwtToken, claims -> {

            Object authorities = claims.get("authorities");

            if (!(authorities instanceof List<?> list) || list.size() != 1) {
                return null;
            }

            String roleName = list.get(0).toString();
            return EnumUserRole.valueOf(roleName);
        });

        if (role == null) {
            throw new NotFoundException();
        }
        return role;
    }

    public Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    // ========================= Token Logic =========================
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        // Extract authorities from UserDetails and convert them to a list of strings
        List<String> authorities = userDetails.getAuthorities().stream().map(auth -> auth.getAuthority()).toList();

        // Add authorities to extra claims
        extraClaims.put("authorities", authorities);

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();
    }

    public boolean isTokenValid(String jwtToken) {
        return !isTokenExpired(jwtToken);
    }

    public boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    // ========================= Claims Logic =========================
    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    // ========================= Key Logic =========================
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
