package com.example.demo.services.impl;

import com.example.demo.dto.ProfileRequestDto;
import com.example.demo.dto.ProfileResponseDto;
import com.example.demo.mappers.ProfileMapper;
import com.example.demo.model.AccountId;
import com.example.demo.model.Authority;
import com.example.demo.model.Profile;
import com.example.demo.repository.AccountIdRepository;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.repository.UserXmlRepository;
import com.example.demo.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final AccountIdRepository accountIdRepository;
    private final ProfileMapper profileMapper;
    private final UserXmlRepository userXmlRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository,
                              AccountIdRepository accountIdRepository,
                              ProfileMapper profileMapper,
                              UserXmlRepository userXmlRepository) {
        this.profileRepository = profileRepository;
        this.accountIdRepository = accountIdRepository;
        this.profileMapper = profileMapper;
        this.userXmlRepository = userXmlRepository;
    }

    @Override
    public ProfileResponseDto getProfileByLogin(String login) {
        final Profile profile = profileRepository.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (profile.isFrozen()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This profile is frozen");
        }
        return profileMapper.toDto(profile);
    }

    @Override
    public List<ProfileResponseDto> getAllProfiles(int page, int pageSize) {
        final Pageable pageable = PageRequest.of(page, pageSize);
        return profileRepository.findAll(pageable)
                .stream()
                .filter(profile -> !profile.isFrozen())
                .map(profileMapper::toDto)
                .toList();
    }

    @Override
    public void edit(Long id, ProfileRequestDto profileRequestDto, AccountId accountId) {
        final Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (profile.getAccountId().equals(accountId)
                || userXmlRepository.findByLogin(profile.getLogin()).getAuthorities().contains(Authority.EDIT_ANY_PROFILE)) {
            final Profile updated = profileMapper.toEntity(profileRequestDto);
            updated.setId(profile.getId());
            updated.setAccountId(accountId);
            accountId.setProfile(profileRepository.save(updated));
            accountIdRepository.save(accountId);
            for (Profile familyMember : profile.getFamily()) {
                final Set<Profile> family = familyMember.getFamily();
                family.add(updated);
                familyMember.setFamily(family);
                profileRepository.save(familyMember);
            }
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @Override
    public void block(Long id, AccountId accountId) {
        final Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (accountId.equals(profile.getAccountId()) ||
                userXmlRepository.findByLogin(profile.getLogin()).getAuthorities().contains(Authority.BLOCK_PROFILE)){
            profile.setFrozen(true);
            profileRepository.save(profile);
        }
    }
}
