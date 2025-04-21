package com.example.demo.mappers.impl;

import com.example.demo.dto.AccountIdDto;
import com.example.demo.dto.RegisterDto;
import com.example.demo.mappers.AccountIdMapper;
import com.example.demo.model.AccountId;
import org.springframework.stereotype.Service;

@Service
public class AccountIdMapperImpl implements AccountIdMapper {
    @Override
    public AccountId toEntity(RegisterDto registerDto) {
        return AccountId.builder()
                .login(registerDto.getLogin())
                .name(registerDto.getName())
                .surname(registerDto.getSurname())
                .sex(registerDto.getSex())
                .birthDate(registerDto.getBirthDate())
                .build();
    }

    @Override
    public AccountIdDto toDto(AccountId accountId) {
        return AccountIdDto.builder()
                .login(accountId.getLogin())
                .name(accountId.getName())
                .surname(accountId.getSurname())
                .sex(accountId.getSex())
                .birthDate(accountId.getBirthDate())
                .build();
    }
}
