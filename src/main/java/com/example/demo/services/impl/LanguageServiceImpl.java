package com.example.demo.services.impl;

import com.example.demo.dto.LanguageDto;
import com.example.demo.mappers.LanguageMapper;
import com.example.demo.repository.LanguageRepository;
import com.example.demo.services.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;

    @Autowired
    public LanguageServiceImpl(LanguageRepository languageRepository,
                               LanguageMapper languageMapper) {
        this.languageRepository = languageRepository;
        this.languageMapper = languageMapper;
    }

    @Override
    public List<LanguageDto> getAll(int page, int pageSize) {
        final Pageable pageable = PageRequest.of(page, pageSize);
        return languageRepository.findAll(pageable)
                .stream()
                .map(languageMapper::toDto)
                .toList();
    }

    @Override
    public LanguageDto create(LanguageDto languageDto) {
        return languageMapper.toDto(
                languageRepository.save(languageMapper.toEntity(languageDto))
        );
    }
}
