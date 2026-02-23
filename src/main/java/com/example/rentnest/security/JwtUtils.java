package com.example.rentnest.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.jwt.secret}")
    private String jwtSecret;
    @Value("${spring.jwt.expiration}")
    private int jwtExpirationMs;

    //Tao ham gen JWT token
    public String generateJwtToken(Authentication authentication){
        UserDetailsImpl userprincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder().setSubject(userprincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsernameFromJwtToken(String token){
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJwt(token).getBody().getSubject();
    }

    public long getExpirationDateFromJwtToken(String token){
        Date expiration = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJwt(token).getBody().getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    public boolean validateJwtToken(String authToken){
        try{
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return false;
    }
}
