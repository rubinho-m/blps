package com.example.demo.jwt;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.Map;

public class JwtLoginModule implements LoginModule {
    private String token;
    private Subject subject;
    private CallbackHandler callbackHandler;
    private JwtService jwtService;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.jwtService = (JwtService) options.get("jwtService");
    }

    @Override
    public boolean login() throws LoginException {
        NameCallback jwtCallback = new NameCallback("JWT Token: ");
        try {
            callbackHandler.handle(new Callback[]{jwtCallback});
        } catch (IOException | UnsupportedCallbackException e) {
            throw new RuntimeException(e);
        }
        token = jwtCallback.getName();
        try {
            jwtService.validateToken(token);
            return true;
        } catch (Exception e) {
            throw new LoginException();
        }
    }

    @Override
    public boolean commit() {
        Principal principal = (UserPrincipal) () -> token;
        subject.getPrincipals().add(principal);
        return true;
    }
    @Override
    public boolean abort() {
        return false;
    }

    @Override
    public boolean logout() {
        return false;
    }
}
