package com.example.demo.jwt;

public interface JwtService {
    String createToken(String login);

    String getLoginFromJwt(String token);

    void validateToken(String token);
}
