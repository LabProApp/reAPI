package com.api.prospects;

import java.util.List;

import org.springframework.stereotype.Service;

import com.api.enums.MasterEnums;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProspectsService {

    private final ProspectsRepository repository;

    public ProspectsService(ProspectsRepository repository) {
        this.repository = repository;
    }

    public Prospects saveProspect(Prospects prospect) {
        log.info("saveProspect - Saving prospect: name={}, email={}", prospect.getName(), prospect.getEmail());
        Prospects saved = repository.save(prospect);
        log.info("saveProspect - Prospect saved with id={}", saved.getId());
        return saved;
    }

    public List<Prospects> getAllProspects() {
        log.info("getAllProspects - Fetching all prospects");
        List<Prospects> prospects = repository.findAll();
        log.info("getAllProspects - Returned {} prospects", prospects.size());
        return prospects;
    }

    public Prospects getProspectById(Long id) {
        log.info("getProspectById - Fetching prospect id={}", id);
        Prospects prospect = repository.findById(id).orElse(null);
        if (prospect == null) {
            log.warn("getProspectById - Prospect not found for id={}", id);
        }
        return prospect;
    }

    public Prospects updateProspectStatus(Long id, MasterEnums.InquiryStatus status) {
        log.info("updateProspectStatus - Updating prospect id={} to status={}", id, status);
        Prospects prospect = getProspectById(id);
        if (prospect != null) {
            prospect.setStatus(status);
            Prospects updated = repository.save(prospect);
            log.info("updateProspectStatus - Prospect id={} status updated to {}", id, status);
            return updated;
        }
        log.warn("updateProspectStatus - Prospect not found for id={}", id);
        return null;
    }
}
