package com.api.version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AppVersionService {

    private static final Logger log = LoggerFactory.getLogger(AppVersionService.class);

    private final AppVersionRepository repository;

    public AppVersionService(AppVersionRepository repository) {
        this.repository = repository;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Core method called by the public check endpoint.
     *
     * Decision table:
     *   clientCode >= latestVersionCode                → no update
     *   latestVersionCode > clientCode >= minSupported → optional update
     *   clientCode < minSupportedVersionCode           → forced update
     */
    public VersionCheckResponse checkVersion(String platform, int clientVersionCode) {
        log.info("checkVersion - platform={} clientVersionCode={}", platform, clientVersionCode);

        Optional<AppVersion> opt =
            repository.findFirstByPlatformIgnoreCaseAndIsActiveTrueOrderByVersionCodeDesc(platform);

        if (opt.isEmpty()) {
            log.info("checkVersion - no active version record for platform={}", platform);
            return VersionCheckResponse.noUpdate();
        }

        AppVersion latest = opt.get();
        boolean updateAvailable = clientVersionCode < latest.getVersionCode();
        boolean forceUpdate     = clientVersionCode < latest.getMinSupportedVersionCode();

        log.info("checkVersion - latestCode={} minSupported={} updateAvailable={} forceUpdate={}",
                latest.getVersionCode(), latest.getMinSupportedVersionCode(),
                updateAvailable, forceUpdate);

        return new VersionCheckResponse(
                updateAvailable,
                forceUpdate,
                latest.getVersionName(),
                latest.getStoreUrl(),
                latest.getReleaseNotes()
        );
    }

    /**
     * Admin: publish a new version.  Deactivates all prior rows for the same
     * platform so that only the latest build is returned by checkVersion.
     */
    @Transactional
    public AppVersion publishVersion(AppVersion incoming) {
        log.info("publishVersion - platform={} versionCode={} versionName={}",
                incoming.getPlatform(), incoming.getVersionCode(), incoming.getVersionName());

        // Deactivate all existing rows for this platform
        List<AppVersion> existing =
            repository.findAllByPlatformIgnoreCase(incoming.getPlatform());
        existing.forEach(v -> v.setIsActive(false));
        repository.saveAll(existing);

        incoming.setIsActive(true);
        AppVersion saved = repository.save(incoming);
        log.info("publishVersion - saved id={}", saved.getId());
        return saved;
    }

    /** Admin: list all version records for all platforms. */
    public List<AppVersion> listAll() {
        return repository.findAll();
    }

    /** Admin: toggle isActive on an existing record. */
    @Transactional
    public AppVersion setActive(Long id, boolean active) {
        AppVersion v = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("AppVersion not found: " + id));
        v.setIsActive(active);
        return repository.save(v);
    }
}
