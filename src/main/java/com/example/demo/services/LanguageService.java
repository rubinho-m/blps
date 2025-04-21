package com.example.demo.services;

import com.example.demo.dto.LanguageDto;

import java.util.List;

public interface LanguageService {
    List<LanguageDto> getAll(int page, int pageSize);

    LanguageDto create(LanguageDto languageDto);
}
