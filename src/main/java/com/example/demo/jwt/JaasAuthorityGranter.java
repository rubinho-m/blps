package com.example.demo.jwt;

import com.example.demo.model.UserXml;
import com.example.demo.repository.UserXmlRepository;
import org.springframework.security.authentication.jaas.AuthorityGranter;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

public class JaasAuthorityGranter implements AuthorityGranter {
    private final JwtService jwtService;
    private final UserXmlRepository userXmlRepository;

    public JaasAuthorityGranter(JwtService jwtService, UserXmlRepository userXmlRepository) {
        this.jwtService = jwtService;
        this.userXmlRepository = userXmlRepository;
    }

    @Override
    public Set<String> grant(Principal principal) {
        final String token = principal.getName();
        final UserXml userXml = userXmlRepository.findByLogin(jwtService.getLoginFromJwt(token));
        final Set<String> authorities =  userXml.getAuthorities().stream()
                .map(Enum::toString)
                .collect(Collectors.toSet());
        return authorities;
    }
}