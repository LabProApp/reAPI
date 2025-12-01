package com.api.prospects;



import java.util.List;

import org.springframework.stereotype.Service;

import com.api.enums.MasterEnums;

@Service
public class ProspectsService {

    private final ProspectsRepository repository;

    public ProspectsService(ProspectsRepository repository) {
        this.repository = repository;
    }

    public Prospects saveProspect(Prospects prospect) {
        return repository.save(prospect);
    }

    public List<Prospects> getAllProspects() {
        return repository.findAll();
    }

    public Prospects getProspectById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Prospects updateProspectStatus(Long id, MasterEnums.InquiryStatus status) {
        Prospects prospect = getProspectById(id);
        if (prospect != null) {
            prospect.setStatus(status);
            return repository.save(prospect);
        }
        return null;
    }
}
