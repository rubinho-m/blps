package com.example.demo.services.impl;

import com.example.demo.dto.TownDto;
import com.example.demo.mappers.TownMapper;
import com.example.demo.repository.TownRepository;
import com.example.demo.services.TownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TownServiceImpl implements TownService {
    private final TownRepository townRepository;
    private final TownMapper townMapper;

    @Autowired
    public TownServiceImpl(TownRepository townRepository,
                           TownMapper townMapper) {
        this.townRepository = townRepository;
        this.townMapper = townMapper;
    }

    @Override
    public List<TownDto> getAll(int page, int pageSize) {
        final Pageable pageable = PageRequest.of(page, pageSize);
        return townRepository.findAll(pageable)
                .stream()
                .map(townMapper::toDto)
                .toList();
    }

    @Override
    public TownDto create(TownDto townDto) {
        return townMapper.toDto(
                townRepository.save(townMapper.toEntity(townDto))
        );
    }
}
