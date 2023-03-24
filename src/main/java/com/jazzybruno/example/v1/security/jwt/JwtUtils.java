package com.jazzybruno.example.v1.security.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class JwtUtils {
    private String jwtSecretKey = "secret@OK";

    public String extractUsername(String token){
        return extractClaim(token , Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token , Claims::getExpiration);
    }

    public boolean hasClaim(String token , String claimName ){
        final Claims claims = extractAllClaims(token);
        return claims.get(claimName) != null;
    }

    public <T> T extractClaim(String token , Function<Claims , T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails user){
        Map<String , Object> claims = new HashMap<>();
        return createToken(claims , user);
    }

        public String createToken(Map<String , Object> claims , UserDetails user){
        return  Jwts.builder().setClaims(claims)
                .setSubject(user.getUsername())
                .claim("authorities" , "admin")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)))
                .signWith(SignatureAlgorithm.HS256 , jwtSecretKey).compact();
    }

    public Boolean isTokenValid(String token , UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
