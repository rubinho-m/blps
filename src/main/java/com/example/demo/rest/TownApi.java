package com.example.demo.rest;

import com.example.demo.dto.TownDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Town Api", description = "Города")
public interface TownApi {
    @GetMapping("/towns")
    ResponseEntity<List<TownDto>> getAll(@RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "size", defaultValue = "10") int pageSize);

    @PostMapping("/towns")
    ResponseEntity<TownDto> create(@RequestBody TownDto townDto);
}
