package com.example.demo.rest;

import com.example.demo.dto.LanguageDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Language Api", description = "Языки")
public interface LanguageApi {
    @GetMapping("/languages")
    ResponseEntity<List<LanguageDto>> getAll(@RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int pageSize);

    @PostMapping("/languages")
    ResponseEntity<LanguageDto> create(@RequestBody LanguageDto languageDto);
}
