package com.example.fakebook;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;

public class JwtDecoder {
    private static final String SECRET_KEY = "this is a key";

    public static String jwtToJson(String jwt) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            return claims.toString(); // Convert Claims object to JSON string
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
