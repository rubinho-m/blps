package com.example.demo.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtServiceImpl implements JwtService {
    @Value("${security.jwt.token.secret-key:secret-value}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String createToken(String login) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + 144_000_000);

        return JWT.create()
                .withIssuer(login)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .sign(Algorithm.HMAC256(secretKey));
    }

    @Override
    public String getLoginFromJwt(String token) {
        return JWT.decode(token).getIssuer();
    }

    @Override
    public void validateToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey))
                .build();
        verifier.verify(token);
//        DecodedJWT decoded = verifier.verify(token);

//        final String login = decoded.getIssuer();
//        final String role = decoded.getSubject();
//
//        return new UsernamePasswordAuthenticationToken(login, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    }
}