package com.example.demo.services.impl;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.RegisterDto;
import com.example.demo.dto.RegisteredAccountIdDto;
import com.example.demo.jwt.JwtService;
import com.example.demo.mappers.AccountIdMapper;
import com.example.demo.model.AccountId;
import com.example.demo.model.Authority;
import com.example.demo.model.Profile;
import com.example.demo.model.UserXml;
import com.example.demo.repository.AccountIdRepository;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.repository.UserXmlRepository;
import com.example.demo.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.CharBuffer;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    private final AccountIdRepository accountIdRepository;
    private final ProfileRepository profileRepository;
    private final AccountIdMapper accountIdMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserXmlRepository userXmlRepository;

    @Autowired
    public AuthServiceImpl(AccountIdRepository accountIdRepository,
                           ProfileRepository profileRepository,
                           AccountIdMapper accountIdMapper,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           UserXmlRepository userXmlRepository) {
        this.accountIdRepository = accountIdRepository;
        this.profileRepository = profileRepository;
        this.accountIdMapper = accountIdMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userXmlRepository = userXmlRepository;
    }

    @Override
    public RegisteredAccountIdDto register(RegisterDto registerDto) {
        if (accountIdRepository.findByLogin(registerDto.getLogin()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Such user already exists");
        }
        final AccountId accountId = accountIdMapper.toEntity(registerDto);
        final Profile createdProfile = new Profile();
        createdProfile.setFrozen(false);
        createdProfile.setLogin(registerDto.getLogin());
        accountId.setProfile(createdProfile);
        accountId.setFrozen(false);
        profileRepository.save(createdProfile);
        accountIdRepository.save(accountId);
        userXmlRepository.save(new UserXml(
                accountId.getLogin(),
                passwordEncoder.encode(registerDto.getPassword()),
                List.of(Authority.BASE)
        ));
        return new RegisteredAccountIdDto(jwtService.createToken(accountId.getLogin()));
    }

    @Override
    public RegisteredAccountIdDto login(LoginDto loginDto) {
        final AccountId accountId = accountIdRepository.findByLogin(loginDto.getLogin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        final UserXml userXml = userXmlRepository.findByLogin(accountId.getLogin());
        if (passwordEncoder.matches(CharBuffer.wrap(loginDto.getPassword()), userXml.getHashedPassword())) {
            checkFrozen(accountId);
            return new RegisteredAccountIdDto(jwtService.createToken(accountId.getLogin()));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
    }

    private void checkFrozen(AccountId accountId) {
        if (accountId.isFrozen() || accountId.getProfile().isFrozen()) {
            final Profile profile = accountId.getProfile();
            profile.setFrozen(false);
            accountId.setFrozen(false);
            accountId.setProfile(profileRepository.save(profile));
            accountIdRepository.save(accountId);
        }
    }
}
