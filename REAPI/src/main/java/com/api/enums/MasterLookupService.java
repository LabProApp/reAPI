package com.api.enums;


import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class MasterLookupService {

    private final MasterLookupRepository repository;
    private final ModelMapper mapper;

    // Constructor Injection (Recommended by Spring)
    public MasterLookupService(MasterLookupRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // Generic master lookup
    public List<MasterLookupDto> getMasterValues(String type, String status) {
        return repository.findByTypeIgnoreCaseAndStatusIgnoreCase(type, status)
                .stream()
                .map(entity -> mapper.map(entity, MasterLookupDto.class))
                .collect(Collectors.toList());
    }

    // Return list of names for child values
    public List<String> getChildByMaster(Long parentId, String status) {
        return repository.findByParentIdAndStatusIgnoreCase(parentId, status)
                .stream()
                .map(entity -> entity.getValue()) // Only extract the "name" field
                .toList();
    }
}

